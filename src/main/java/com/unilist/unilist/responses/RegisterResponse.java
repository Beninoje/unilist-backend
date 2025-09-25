package com.unilist.unilist.responses;


public class RegisterResponse extends AuthResponse{
    private String firstName;
    private String lastName;
    private String email;

    public RegisterResponse(String token, long expiresIn) {
        super(token, expiresIn);
    }

    public RegisterResponse(String token, long expiresIn, String email, String firstName, String lastName) {
        super(token, expiresIn);
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
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
}
