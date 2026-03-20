package com.unilist.campora.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LogoutDto {
    private UUID refreshToken;
}
