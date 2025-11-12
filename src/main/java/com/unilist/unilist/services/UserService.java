package com.unilist.unilist.services;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;

    public UserService(UserRepository userRepository, EmailService emailService, ListingRepository listingRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }
    public List<Listing> getUserFavourites(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Not found"));
        return user.getFavourites();
    }
    public User fetchUserByListingId(Long listingId){
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(()-> new RuntimeException("Listing not found"));

        return listing.getUser();
    }
}
