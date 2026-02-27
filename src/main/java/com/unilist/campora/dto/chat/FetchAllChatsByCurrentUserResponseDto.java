package com.unilist.campora.dto.chat;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FetchAllChatsByCurrentUserResponseDto {
    private UUID chatId;
    private UUID otherUserId;
    private String otherFirstName;
    private String otherLastName;
    private Instant createdAt;
    private String lastMessage;
    private UUID listingId;

}
