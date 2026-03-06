package com.unilist.campora.dto.chat;

import com.unilist.campora.model.Message;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageDto {
    private UUID chatId;
    private UUID senderId;
    private String content;
    private UUID listingId;
    private UUID replyToMessageId;
    private Instant createdAt;

}
