package com.unilist.unilist.responses;


import com.unilist.unilist.model.Listing;

import java.util.List;
import java.util.Set;

public class LoginResponse {
    private Long id;
    private String token;
    private long expiresIn;
    private String firstName;
    private String lastName;
    private String email;
    private List<Long> favourites;

    public LoginResponse(Long id, String token, long expiresIn,String email, String firstName, String lastName,List<Long> favourites) {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getFavourites() {
        return favourites;
    }

    public void setFavourites(List<Long> favourites) {
        this.favourites = favourites;
    }
}
