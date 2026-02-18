package com.unilist.unilist.dto.chat;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChatDto {
    private UUID senderId;
    private String content;
    private UUID sellerId;
    private UUID listingId;
    private Instant createdAt;
}
