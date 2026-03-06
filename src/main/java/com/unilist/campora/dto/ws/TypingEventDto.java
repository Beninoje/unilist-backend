package com.unilist.campora.dto.ws;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingEventDto {
    private UUID userId;
    private UUID chatId;
    private boolean typing;
}
