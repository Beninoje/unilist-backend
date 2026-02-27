package com.unilist.campora.services;

import com.unilist.campora.dto.chat.FetchAllChatsByCurrentUserResponseDto;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.User;
import org.springframework.stereotype.Service;

@Service
public class ChatService {
    public FetchAllChatsByCurrentUserResponseDto mapToDto(Chat chat, User currUser){
        User otherUser = chat.getBuyer().equals(currUser) ? chat.getSeller() : chat.getBuyer();

        String lastMessage = chat.getMessages().isEmpty() ? ""
                : chat.getMessages()
                .get(chat.getMessages().size() - 1)
                .getContent();

        return FetchAllChatsByCurrentUserResponseDto.builder()
                .chatId(chat.getId())
                .otherUserId(otherUser.getId())
                .otherFirstName(otherUser.getFirstName())
                .otherLastName(otherUser.getLastName())
                .createdAt(chat.getCreatedAt())
                .lastMessage(lastMessage)
                .listingId(chat.getListingId())
                .build();

    }
}
