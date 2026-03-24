package com.unilist.campora.services;

import com.unilist.campora.dto.chat.FetchAllChatsByCurrentUserResponseDto;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Message;
import com.unilist.campora.model.User;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class ChatService {
    public FetchAllChatsByCurrentUserResponseDto mapToDto(Chat chat, User currUser){
        User otherUser = chat.getBuyer().equals(currUser) ? chat.getSeller() : chat.getBuyer();

        Message lastMessage = chat.getMessages().stream()
                .max(Comparator.comparing(Message::getCreatedAt))
                .orElse(null);

        String lastContent = chat.getMessages().isEmpty() ? ""
                : chat.getMessages()
                .get(chat.getMessages().size() - 1)
                .getContent();

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
}
