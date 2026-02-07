package com.unilist.unilist.responses;


import com.unilist.unilist.model.Listing;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class LoginResponse {
    private UUID id;
    private String token;
    private long expiresIn;
    private String firstName;
    private String lastName;
    private String email;
    private List<UUID> favourites;

    public LoginResponse(UUID id, String token, long expiresIn,String email, String firstName, String lastName,List<UUID> favourites) {
        this.id = id;
        this.token = token;
        this.expiresIn = expiresIn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.favourites = favourites;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<UUID> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<UUID> favourites) {
        this.favourites = favourites;
    }
}
