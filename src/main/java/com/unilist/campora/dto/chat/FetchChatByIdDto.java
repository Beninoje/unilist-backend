package com.unilist.campora.dto.chat;

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
    private String buyerName;
    private String sellerName;
    private List<Message> messages;
}
