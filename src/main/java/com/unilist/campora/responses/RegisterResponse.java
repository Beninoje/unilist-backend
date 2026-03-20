package com.unilist.campora.responses;


import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Listing;

import java.util.List;
import java.util.Set;
import java.util.UUID;

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

    public RegisterResponse(String token, long expiresIn) {
        super(token, expiresIn);
    }

    public RegisterResponse(String token, long expiresIn, String email, String firstName, String lastName, UUID refreshToken,Boolean onboardingComplete, List<Listing>listings, List<UUID> favourites, Double latitude, Double longitude) {
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
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(UUID refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Boolean getOnboardingComplete() {
        return onboardingComplete;
    }

    public void setOnboardingComplete(Boolean onboardingComplete) {
        this.onboardingComplete = onboardingComplete;
    }

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }

    public List<UUID> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<UUID> favourites) {
        this.favourites = favourites;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
