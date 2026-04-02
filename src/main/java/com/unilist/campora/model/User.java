package com.unilist.campora.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false, name="first_name")
    private String firstName;

    @Column(nullable = false, name="last_name")
    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(name="profile_img")
    private String profileImage;

    @Column(name="verification_code")
    private String verificationCode;

    @Column(name="verification_expire")
    private LocalDateTime verificationExpiresAt;

    @Column(name="postal_code")
    private String postalCode;

    @Column(name="latitude")
    private Double latitude;

    @Column(name="longitude")
    private Double longitude;

    @Column(name="user_enabled")
    private boolean enabled;

    @Column(name="otp_verified")
    private boolean optVerified;

    @Column(name="onboarding_completed")
    private boolean onboardingComplete;

    @Column(name = "campus_type")
    private String campusType;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Listing> listings;

    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Report> reports;

    @ManyToMany
    @JoinTable(
            name = "user_favourites",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "listing_id")
    )
    private Set<Listing> favourites = new HashSet<>();

    @JsonManagedReference
    @OneToMany(mappedBy = "buyer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Chat> chats;

    @ElementCollection
    @CollectionTable(name = "user_push_tokens", joinColumns = @JoinColumn(name = "user_id"))
    @OneToMany(mappedBy ="user", fetch = FetchType.EAGER,  cascade = CascadeType.ALL)
    private Set<PushToken> pushTokens = new HashSet<>();


    public User(String email, String password, String firstName, String lastName, List<Listing> listings) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.listings = listings;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of();
    }


    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return password;
    }
    public boolean getOtpVerified(){
        return optVerified;
    }




}
