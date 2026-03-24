package com.unilist.campora.controllers;

import com.unilist.campora.dto.chat.ChatMessageDto;
import com.unilist.campora.dto.chat.CreateChatDto;
import com.unilist.campora.dto.chat.FetchChatByIdDto;
import com.unilist.campora.dto.ws.ReadChatDto;
import com.unilist.campora.dto.ws.TypingEventDto;
import com.unilist.campora.dto.ws.UnreadCountDto;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Listing;
import com.unilist.campora.model.Message;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ChatRepository;
import com.unilist.campora.repository.ListingRepository;
import com.unilist.campora.repository.MessageRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.ws.SendMessageResponse;
import com.unilist.campora.services.UserService;
import com.unilist.campora.services.chat.MessageService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/chats")
public class ChatController {
    private final SimpMessagingTemplate messageTemplate;
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public ChatController(SimpMessagingTemplate messageTemplate, MessageService messageService, MessageRepository messageRepository, ChatRepository chatRepository, UserRepository userRepository, ListingRepository listingRepository) {
        this.messageTemplate = messageTemplate;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDto incomingMessage){

        Chat chat = chatRepository.findById(incomingMessage.getChatId())
                        .orElseThrow(()-> new IllegalArgumentException("Chat does not exist"));

        if(!chat.hasUser(incomingMessage.getSenderId())){
            throw new IllegalArgumentException("User is not in the chat");
        }

        User user = userRepository.findById(incomingMessage.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Message replyTo = null;

        if(incomingMessage.getReplyToMessageId() != null){
            replyTo = messageRepository.findById(incomingMessage.getReplyToMessageId())
                    .orElse(null);
        }

        Message message = Message.builder()
                        .chat(chat)
                        .sender(user)
                        .content(incomingMessage.getContent())
                        .replyTo(replyTo)
                        .isInitial(false)
                        .read(false)
                        .createdAt(Instant.now())
                        .build();

        messageRepository.save(message);
        UUID replyToId = message.getReplyTo() != null ? message.getReplyTo().getId() : null;
        String replyToContent = message.getReplyTo() != null ? message.getReplyTo().getContent() : null;
        UUID replyToSenderId = message.getReplyTo() != null ? message.getSender().getId() : null;
        String replyToSenderFirstName = message.getReplyTo() != null ? message.getReplyTo().getSender().getFirstName() : null;
        String replyToSenderLastName = message.getReplyTo() != null ? message.getReplyTo().getSender().getLastName() : null;

        SendMessageResponse sendMessageResponse = new SendMessageResponse(
                chat.getId(),
                message.getId(),
                user.getId(),
                message.getContent(),
                chat.getListingId(),
                replyToId,
                replyToContent,
                replyToSenderId,
                replyToSenderFirstName,
                replyToSenderLastName,
                message.getIsInitial(),
                message.getRead(),
                message.getCreatedAt()
        );

        if (!chat.getBuyer().getId().equals(user.getId())) {
            List<Object[]> buyerUnread = messageRepository.getUnreadCountsPerChat(chat.getBuyer().getId());
            messageTemplate.convertAndSend("/topic/user/" + chat.getBuyer().getId() + "/chat", sendMessageResponse);
            messageTemplate.convertAndSend("/topic/user/" + chat.getBuyer().getId() + "/unread", buyerUnread);
        }
        if (!chat.getSeller().getId().equals(user.getId())) {
            List<Object[]> sellerUnread = messageRepository.getUnreadCountsPerChat(chat.getSeller().getId());
            messageTemplate.convertAndSend("/topic/user/" + chat.getSeller().getId() + "/chat", sendMessageResponse);
            messageTemplate.convertAndSend("/topic/user/" + chat.getSeller().getId() + "/unread", sellerUnread);
        }
        messageTemplate.convertAndSend(
                "/topic/chat/" + chat.getId(),
                sendMessageResponse
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(TypingEventDto typingEventDto){
        messageTemplate.convertAndSend(
                "/topic/chat/" + typingEventDto.getChatId() + "/typing",
                typingEventDto
        );
    }
    @MessageMapping("/chat.read")
    public void markChatAsRead(@Payload ReadChatDto readChatDto){
        UUID chatId = readChatDto.getChatId();
        UUID readerId = readChatDto.getReaderId();

        messageRepository.markMessageAsRead(chatId,readerId);
        List<Object[]> updatedUnread = messageRepository.getUnreadCountsPerChat(readerId);
        System.out.println("Publishing unread to: /topic/user/" + readerId + "/unread");
        System.out.println("Unread data: " + updatedUnread.size() + " chats");
        messageTemplate.convertAndSend(
                "/topic/user/" + readerId + "/unread",
                updatedUnread
        );
        messageTemplate.convertAndSend("/topic/chat/" + chatId + "/read", readChatDto);
    }


    @PostMapping("/send")
    @CacheEvict(value="listing_owner", key="#incomingMsg.listingId")
    public ResponseEntity<?> createChat(@RequestBody CreateChatDto incomingMsg){
        User buyer = userRepository.findById(incomingMsg.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("Buyer not found"));
        User seller = userRepository.findById(incomingMsg.getSellerId())
                .orElseThrow(() -> new IllegalArgumentException("Seller not found"));
        if(buyer.getId().equals(seller.getId())){
            return ResponseEntity.status(400).body("You cannot create a chat with yourself");
        }

        boolean chatExists = chatRepository.existsByBuyerIdAndSellerIdAndListingId(
                buyer.getId(),
                incomingMsg.getSellerId(),
                incomingMsg.getListingId()
        );
        if(chatExists){
            return ResponseEntity.badRequest().body("Cannot create a chat with the same buyer,seller and listing ID");
        }
        Chat chat = Chat.builder()
                .buyer(buyer)
                .seller(seller)
                .listingId(incomingMsg.getListingId())
                .createdAt(Instant.now())
                .build();
        chatRepository.save(chat);

        Message message = Message.builder()
                .chat(chat)
                .sender(buyer)
                .content(incomingMsg.getContent())
                .isInitial(true)
                .read(false)
                .createdAt(Instant.now())
                .build();

        messageRepository.save(message);
        SendMessageResponse msgRes = new SendMessageResponse(
                chat.getId(),
                message.getId(),
                buyer.getId(),
                message.getContent(),
                chat.getListingId(),
                null,null,null,null,null,
                true,
                false,
                message.getCreatedAt()
        );

        messageTemplate.convertAndSend("/topic/user/"+seller.getId()+"/chat",msgRes);

        return ResponseEntity.ok(chat.getId());
    }


    @GetMapping("/get/{id}")
    public ResponseEntity<FetchChatByIdDto> getChat(@PathVariable UUID id){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        Chat chat = chatRepository.findChatForUser(id,currentUser.getId())
                .orElseThrow(()-> new IllegalArgumentException("Chat does not exist or access denied"));
        Listing listing = listingRepository.findById(chat.getListingId()).orElseThrow(() -> new IllegalArgumentException("Listing does not exist anymore"));
        User otherUser;
        if(chat.getBuyer().getId().equals(currentUser.getId())){
            otherUser = chat.getSeller();
        }
        else{
            otherUser = chat.getBuyer();
        }
        return ResponseEntity.ok(new FetchChatByIdDto(
                chat.getId(),
                otherUser.getFirstName(),
                otherUser.getLastName(),
                otherUser.getId(),
                otherUser.getProfileImage(),
                listing.getId(),
                listing.getImages(),
                listing.getTitle(),
                listing.getStatus(),
                messageService.getAllMessagesByChat(chat)

        ));
    }


}
