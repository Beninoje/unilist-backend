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

        Message lastMessage = null;
        if (!chat.getMessages().isEmpty()) {
            lastMessage = chat.getMessages().get(chat.getMessages().size() - 1);
        }
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
}
