package com.unilist.campora.dto.messages;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponseDto {

    private UUID messageId;
    private UUID senderId;
    private String senderFirstName;
    private String senderLastName;
    private String content;

    private UUID replyToMessageId;
    private String replyToContent;
    private UUID replyToSenderId;
    private String replyToSenderFirstName;
    private String replyToSenderLastName;


    private Instant createdAt;
}
