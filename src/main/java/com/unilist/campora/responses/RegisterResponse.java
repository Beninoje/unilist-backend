package com.unilist.campora.responses;


import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Listing;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class RegisterResponse extends AuthResponse{
    private String firstName;
    private String lastName;
    private String email;
    private UUID refreshToken;
    private Boolean onboardingComplete;
    private List<Listing> listings;
    private List<UUID> favourites;
    private Double latitude;
    private Double longitude;
    private String postalCode;
    private String campusType;


    public RegisterResponse(String token, long expiresIn) {
        super(token, expiresIn);
    }

    public RegisterResponse(String token, long expiresIn, String email, String firstName, String lastName,
                            UUID refreshToken,Boolean onboardingComplete, List<Listing>listings, List<UUID> favourites,
                            Double latitude, Double longitude,String postalCode, String campusType) {
        super(token, expiresIn);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.refreshToken = refreshToken;
        this.onboardingComplete = onboardingComplete;
        this.listings = listings;
        this.favourites = favourites;
        this.latitude = latitude;
        this.longitude = longitude;
        this.postalCode = postalCode;
        this.campusType = campusType;
    }


}
