package com.unilist.unilist.services;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
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
}
