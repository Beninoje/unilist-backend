package com.unilist.unilist.dto.chat;

import com.unilist.unilist.model.Chat;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.Message;
import com.unilist.unilist.model.User;
import com.unilist.unilist.type.MessageType;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private Long chatId;
    private Long senderId;
    private String content;
    private Long listingId;
    private Instant createdAt;

}
