package com.unilist.unilist.controllers;

import com.unilist.unilist.dto.UpdateUserDto;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.repository.UserRepository;
import com.unilist.unilist.responses.UpdateUserResponse;
import com.unilist.unilist.responses.UserResponse;
import com.unilist.unilist.security.CustomJwtUser;
import com.unilist.unilist.services.UserService;
import com.unilist.unilist.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ListingRepository listingRepository;

    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder, ListingRepository listingRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.listingRepository = listingRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        UserResponse userResponse = new UserResponse(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getPassword(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getFavourites().stream().map(Listing::getId).toList(),
                currentUser.getListings().stream().map(Listing::getId).toList()
        );
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserProfile( @RequestBody UpdateUserDto body){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));


        if (body.getFirstName() != null && !body.getFirstName().isEmpty()) {
            currentUser.setFirstName(body.getFirstName());
        }

        if (body.getLastName() != null && !body.getLastName().isEmpty()) {
            currentUser.setLastName(body.getLastName());
        }

        if(body.getPassword() != null && !body.getPassword().isEmpty()){
            currentUser.setPassword(passwordEncoder.encode(body.getPassword()));
        }

        User updatedUser = userRepository.save(currentUser);

        return ResponseEntity.ok(
                new UpdateUserResponse(
                        updatedUser.getFirstName(),
                        updatedUser.getLastName(),
                        updatedUser.getPassword()
                )
        );

    }

    @Transactional
    @PostMapping("/favourites/{id}")
    public ResponseEntity<?> addToFavourites(@PathVariable UUID id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        boolean exists = currentUser.getFavourites().stream()
                .anyMatch(fav -> fav.getId().equals(listing.getId()));


        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Listing already added to favourites");
        }

        currentUser.getFavourites().add(listing);
        userRepository.save(currentUser);


        return ResponseEntity.ok("Listing added to favourites");
    }

    @GetMapping("/favourites/all")
    public ResponseEntity<?> fetchAllFavourites(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        Set<Listing> allFavListings = userService.getUserFavourites(currentUser.getId());

        if (allFavListings.isEmpty()){
            return ResponseEntity.badRequest().body("You have no favourites");
        }
        return ResponseEntity.ok().body(allFavListings);

    }

    @DeleteMapping("/favourites/{id}")
    public ResponseEntity<?> removeFavourite(@PathVariable UUID id){
        Optional<Listing> currentListing = listingRepository.findById(id);
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        currentUser.getFavourites().removeIf(l -> l.getId().equals(currentListing.get().getId()));
        userRepository.save(currentUser);
        return ResponseEntity.ok().body("Listing successfully removed from your favourites");

    }

    @GetMapping("/listings/all")
    public ResponseEntity<List<Listing>> fetchUserListings(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        List<Listing> userListings = currentUser.getListings().stream().sorted(Comparator.comparing(Listing::getCreatedAt).reversed()).toList();
        return ResponseEntity.ok().body(userListings);
    }


}
