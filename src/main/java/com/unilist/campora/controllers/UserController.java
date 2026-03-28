package com.unilist.campora.controllers;

import com.unilist.campora.dto.CompleteOnboardingDto;
import com.unilist.campora.dto.UpdateUserDto;
import com.unilist.campora.dto.chat.FetchAllChatsByCurrentUserResponseDto;
import com.unilist.campora.model.*;
import com.unilist.campora.repository.ChatRepository;
import com.unilist.campora.repository.ListingRepository;
import com.unilist.campora.repository.RefreshTokenRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.CompletedOnboardingResponse;
import com.unilist.campora.responses.UpdateUserResponse;
import com.unilist.campora.responses.UserResponse;
import com.unilist.campora.services.ChatService;
import com.unilist.campora.services.GoogleGeoService;
import com.unilist.campora.services.JwtService;
import com.unilist.campora.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RequestMapping("/users")
@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ListingRepository listingRepository;
    private final ChatRepository chatRepository;
    private final ChatService chatService;
    private final GoogleGeoService googleGeoService;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;


    public UserController(UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder, ListingRepository listingRepository, ChatRepository chatRepository, ChatService chatService, GoogleGeoService googleGeoService, JwtService jwtService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.listingRepository = listingRepository;
        this.chatRepository = chatRepository;
        this.chatService = chatService;
        this.googleGeoService = googleGeoService;
        this.jwtService = jwtService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        UserResponse userResponse = new UserResponse(
                currentUser.getId(),
                currentUser.getEmail(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getFavourites().stream().map(Listing::getId).toList(),
                currentUser.getListings().stream().map(Listing::getId).toList(),
                currentUser.isOnboardingComplete(),
                currentUser.getLatitude(),
                currentUser.getLongitude(),
                currentUser.getPostalCode(),
                currentUser.getCampusType(),
                currentUser.getOtpVerified(),
                currentUser.getProfileImage(),
                currentUser.isEnabled()
        );
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/me/onboarding")
    public ResponseEntity<?> completeOnboarding(@RequestBody CompleteOnboardingDto body){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));
        if(!body.getCampusType().equals("ORILLIA") && !body.getCampusType().equals("THUNDERBAY")){
            throw new RuntimeException("Incorrect campus!");
        }
        double[] coor = googleGeoService.getCoordinatesFromPostalCode(body.getPostalCode());
        currentUser.setLatitude(coor[0]);
        currentUser.setLongitude(coor[1]);
        currentUser.setPostalCode(body.getPostalCode());
        currentUser.setCampusType(body.getCampusType());
        currentUser.setOnboardingComplete(true);

        currentUser = userRepository.save(currentUser);
        String token = jwtService.generateToken(currentUser);

        return ResponseEntity.ok(new CompletedOnboardingResponse(
                currentUser.getId(),
                token,
                jwtService.getJwtExpiration(),
                currentUser.getFirstName(),
                currentUser.getLastName(),
                currentUser.getEmail(),
                currentUser.getFavourites().stream().map(Listing::getId).toList(),
                currentUser.getLatitude(),
                currentUser.getLongitude(),
                currentUser.isOnboardingComplete()

        ));



    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        return ResponseEntity.ok(allUsers);

    }

    @PutMapping("/me/update")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateUserDto body){
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

        if(body.getProfileImage() != null){
            currentUser.setProfileImage(body.getProfileImage().isBlank() ? null : body.getProfileImage());
        }
        if(body.getPostalCode() != null && !body.getPostalCode().isEmpty()){
                double[] coor = googleGeoService.getCoordinatesFromPostalCode(body.getPostalCode());
                currentUser.setLatitude(coor[0]);
                currentUser.setLongitude(coor[1]);
                currentUser.setPostalCode(body.getPostalCode());
                currentUser.setCampusType(body.getCampusType());
        }


        User updatedUser = userRepository.save(currentUser);

        return ResponseEntity.ok(
                new UpdateUserResponse(
                        updatedUser.getFirstName(),
                        updatedUser.getLastName(),
                        updatedUser.getPostalCode(),
                        updatedUser.getLatitude(),
                        updatedUser.getLongitude(),
                        updatedUser.getCampusType(),
                        updatedUser.getProfileImage()
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

    @GetMapping("/chats/all")
    public List<FetchAllChatsByCurrentUserResponseDto> fetchAllChats(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        return chatRepository.findAllByBuyerOrSeller(currUser).stream()
                .sorted(Comparator.comparing(
                            chat -> {
                                Message lastMessage = chat.getMessages().stream()
                                        .max(Comparator.comparing(Message::getCreatedAt))
                                        .orElse(null);
                                return lastMessage != null ? lastMessage.getCreatedAt() : chat.getCreatedAt();
                            },
                        Comparator.nullsLast(Comparator.naturalOrder())

                ))
                .map(chat -> chatService.mapToDto(chat, currUser))
                .toList();
    }

}
