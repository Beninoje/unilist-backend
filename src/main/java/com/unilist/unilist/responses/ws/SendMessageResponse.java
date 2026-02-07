package com.unilist.unilist.responses.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
