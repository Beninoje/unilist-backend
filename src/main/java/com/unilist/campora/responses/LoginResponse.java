package com.unilist.campora.responses;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private UUID id;
    private String token;
    private long expiresIn;
    private UUID refreshToken;
    private String firstName;
    private String lastName;
    private String email;
    private List<UUID> favourites;
    private Double latitude;
    private Double longitude;
    private String postalCode;
    private String campusType;
    private boolean onboardingComplete;
    private String profileImage;
    private boolean otpVerified;
    private boolean enabled;


}
