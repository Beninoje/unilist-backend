package com.unilist.campora.dto.chat;

import com.unilist.campora.dto.messages.MessageResponseDto;
import com.unilist.campora.model.Message;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchChatByIdDto {
    private UUID id;
    private String otherFirstName;
    private String otherLastName;
    private UUID otherUserId;
    private String otherUserProfileImage;
    private UUID listingId;
    private List<String> listingImages;
    private String listingName;
    private String listingStatus;
    private Double listingPrice;
    private List<MessageResponseDto> messages;
}
