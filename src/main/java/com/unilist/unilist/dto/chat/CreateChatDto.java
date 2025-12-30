package com.unilist.unilist.dto.chat;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateChatDto {
    private Long senderId;
    private String content;
    private Long listingId;
    private Instant createdAt;
}
