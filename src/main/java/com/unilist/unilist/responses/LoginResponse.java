package com.unilist.unilist.responses;


import com.unilist.unilist.model.Listing;

import java.util.List;

public class LoginResponse {
    private Long id;
    private String token;
    private long expiresIn;
    private String firstName;
    private String lastName;
    private String email;
    private List<Listing> listings;

    public LoginResponse(Long id, String token, long expiresIn,String email, String firstName, String lastName, List<Listing> listings) {
        this.id = id;
        this.token = token;
        this.expiresIn = expiresIn;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.listings = listings;
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

    public List<Listing> getListings() {
        return listings;
    }

    public void setListings(List<Listing> listings) {
        this.listings = listings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
