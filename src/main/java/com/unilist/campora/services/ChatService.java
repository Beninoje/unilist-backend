package com.unilist.campora.services;

import com.unilist.campora.dto.chat.FetchAllChatsByCurrentUserResponseDto;
import com.unilist.campora.dto.notifications.SendPushNotification;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Message;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ChatRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public FetchAllChatsByCurrentUserResponseDto mapToDto(Chat chat, User currUser){
        User otherUser = chat.getBuyer().equals(currUser) ? chat.getSeller() : chat.getBuyer();

        Message lastMessage = chat.getMessages().stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .orElse(null);
        String lastContent = lastMessage != null ? lastMessage.getContent() : null;

        return FetchAllChatsByCurrentUserResponseDto.builder()
                .chatId(chat.getId())
                .otherUserId(otherUser.getId())
                .otherFirstName(otherUser.getFirstName())
                .otherLastName(otherUser.getLastName())
                .otherUserProfileImage(otherUser.getProfileImage())
                .createdAt(lastMessage != null ? lastMessage.getCreatedAt() : chat.getCreatedAt())
                .lastMessage(lastContent)
                .listingId(chat.getListingId())
                .build();

    }

    public void sendPushNotification(SendPushNotification body){
        System.out.println("Token: "+body.getPushToken());
        System.out.println("First Name: "+body.getSenderFirstName());
        String url = "https://exp.host/--/api/v2/push/send";
        Map<String,Object> message = new HashMap<>();
        message.put("to",body.getPushToken());
        message.put("sound","default");
        message.put("title","Campora");
        message.put("body",body.getSenderFirstName()+": "+body.getContent());

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            String response = restTemplate.postForObject(url, message, String.class);
            System.out.println("Expo response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean chatExists(UUID buyerId, UUID sellerId, UUID listingId) {
        return chatRepository.existsByBuyerIdAndSellerIdAndListingId(
                buyerId,
                sellerId,
                listingId
        );
    }

    public Chat getChat(UUID buyerId, UUID sellerId, UUID listingId) {
        return chatRepository.findChatByBuyerIdAndSellerIdAndListingId(
                buyerId,
                sellerId,
                listingId
        );
    }
}
