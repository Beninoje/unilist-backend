package com.unilist.campora.responses.ws;

import com.unilist.campora.model.Message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageResponse {
    private UUID chatId;
    private UUID messageId;
    private UUID senderId;
    private String content;
    private UUID listingId;

    private UUID replyToMessageId;
    private String replyToContent;
    private UUID replyToSenderId;
    private String replyToSenderFirstName;
    private String replyToSenderLastName;
    private Boolean isInitial;
    private boolean read;
    private Instant createdAt;
}
