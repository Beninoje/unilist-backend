package com.unilist.unilist.controllers;

import com.unilist.unilist.dto.UpdateUserDto;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.repository.UserRepository;
import com.unilist.unilist.responses.UpdateUserResponse;
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

import java.util.List;
import java.util.Optional;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        if (!authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(currentUser);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);

    }

    @PutMapping("/update")
    public ResponseEntity<?> updateUserProfile( @RequestBody UpdateUserDto body){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        if (!authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        User currentUser = (User) authentication.getPrincipal();


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
    public ResponseEntity<?> addToFavourites(@PathVariable Long id){
        User currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }


        Listing listing = listingRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found")
        );

        boolean isListingExists = currentUser.getFavourites().stream().
                anyMatch(fav -> fav.getId().equals(listing.getId()));

        if(isListingExists){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Listing is already in favourites");
        }

        currentUser.getFavourites().add(listing);
        userRepository.save(currentUser);

        return ResponseEntity.ok("Listing successfully added to favourites");

    }

    @GetMapping("/favourites/all")
    public ResponseEntity<?> fetchAllFavourites(){
        User currentUser = SecurityUtils.getCurrentUser();

        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        List<Listing> allFavListings = userService.getUserFavourites(currentUser.getId());

        if (allFavListings.isEmpty()){
            return ResponseEntity.badRequest().body("You have no favourites");
        }
        return ResponseEntity.ok().body(allFavListings);

    }


}
