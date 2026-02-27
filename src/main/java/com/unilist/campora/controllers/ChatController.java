package com.unilist.campora.controllers;

import com.unilist.campora.dto.chat.ChatMessageDto;
import com.unilist.campora.dto.chat.CreateChatDto;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Message;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ChatRepository;
import com.unilist.campora.repository.MessageRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.ws.SendMessageResponse;
import com.unilist.campora.services.UserService;
import com.unilist.campora.services.chat.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/chats")
public class ChatController {
    private final SimpMessagingTemplate messageTemplate;
    private final MessageService messageService;
    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public ChatController(SimpMessagingTemplate messageTemplate, MessageService messageService, MessageRepository messageRepository, ChatRepository chatRepository, UserRepository userRepository, UserService userService) {
        this.messageTemplate = messageTemplate;
        this.messageService = messageService;
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.userService = userService;
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

        Message message = Message.builder()
                        .chat(chat)
                        .sender(user)
                        .content(incomingMessage.getContent())
                        .createdAt(Instant.now())
                        .build();

        messageRepository.save(message);

        SendMessageResponse sendMessageResponse = new SendMessageResponse(
                chat.getId(),
                message.getId(),
                user.getId(),
                message.getContent(),
                chat.getListingId()

        );

        messageTemplate.convertAndSend(
                "/topic/chat/" + chat.getId(),
                sendMessageResponse
        );
    }

//    @MessageMapping("/chat.addUser")
//    @SendTo("/topic/public")
//    public ChatMessageDto addUser(
//            @RequestBody ChatMessageDto message,
//            SimpMessageHeaderAccessor headerAccessor
//    ){
//        // Add user to websocket session
//        headerAccessor.getSessionAttributes().put("username",message.getSender());
//        return message;
//    }
    @PostMapping("/send")
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
                .createdAt(Instant.now())
                .build();
        messageRepository.save(message);

        return ResponseEntity.ok(chat.getId());
    }


//    @GetMapping("/get/{id}")
//    public ResponseEntity<?> getChat(@PathVariable UUID id){
//        Chat chat = chatRepository.findById(id)
//                .orElseThrow(()-> new IllegalArgumentException("Chat does not exist"));
//
//        FetchChatByIdDto chatResponse = new FetchChatByIdDto(
//                chat.getBuyer().getFirstName(),
//                chat.getSeller().getFirstName(),
//                chat.getMessages()
//        );
//        return ResponseEntity.ok(chatResponse);
//    }
}
