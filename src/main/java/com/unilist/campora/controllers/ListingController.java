package com.unilist.campora.controllers;

import com.unilist.campora.dto.listing.CreateListingDto;
import com.unilist.campora.dto.listing.EditListingDto;
import com.unilist.campora.dto.listing.ListingOwnerDTO;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Listing;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ListingRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.ListingResponse;
import com.unilist.campora.responses.PageableListingResponse;
import com.unilist.campora.responses.listings.EditListingResponse;
import com.unilist.campora.services.ChatService;
import com.unilist.campora.services.ListingService;
import com.unilist.campora.services.UserService;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@EnableCaching
@RequestMapping("/listing")
@RestController
public class ListingController {

    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ChatService chatService;

    public ListingController(ListingRepository listingRepository, ListingService listingService, UserRepository userRepository, UserService userService, ChatService chatService) {
        this.listingRepository = listingRepository;
        this.listingService = listingService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.chatService = chatService;
    }

    @CacheEvict(value = "listings", allEntries = true)
    @PostMapping("/create")
    public ResponseEntity<List<Listing>> createListing(@RequestBody CreateListingDto body){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        Listing listing = new Listing();
        listing.setTitle(body.getTitle());
        listing.setDescription(body.getDescription());
        listing.setPrice(body.getPrice());
        listing.setCategory(body.getCategory());
        listing.setStatus("active");
        listing.setCondition(body.getCondition());
        listing.setImages(body.getImages());
        listing.setCreatedAt(LocalDateTime.now());
        listing.setUpdatedAt(LocalDateTime.now());
        listing.setUser(currentUser);
        listingRepository.save(listing);

        List<Listing> updatedListings = listingRepository.findByUserId(currentUser.getId());

        return ResponseEntity.ok(updatedListings);

    }

    @PutMapping("/edit/{id}")
    @Caching(evict={
            @CacheEvict(value="listings", allEntries=true),
            @CacheEvict(value="listing", key="#id.toString()")
    })
    public ResponseEntity<EditListingResponse> editListing(@PathVariable UUID id, @RequestBody EditListingDto body){
        Optional<Listing> currentListing = listingRepository.findById(id);

        Listing listing = currentListing.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found")
        );

        if (body.getTitle() != null && !body.getTitle().isEmpty()) {
            listing.setTitle(body.getTitle());
        }

        if (body.getDescription() != null && !body.getDescription().isEmpty()) {
            listing.setDescription(body.getDescription());
        }

        if (body.getPrice() != null && !body.getPrice().isNaN()) {
            listing.setPrice(body.getPrice());
        }

        if (body.getCategory() != null && !body.getCategory().isEmpty()) {
            listing.setCategory(body.getCategory());
        }

        if (body.getStatus() != null && !body.getStatus().isEmpty()) {
            listing.setStatus(body.getStatus());
        }

        if (body.getCondition() != null && !body.getCondition().isEmpty()) {
            listing.setCondition(body.getCondition());
        }

        if (body.getImages() != null && !body.getImages().isEmpty()) {
            listing.setImages(body.getImages());
        }

        if(body.getStatus() != null && !body.getStatus().isBlank()){
            listing.setStatus(body.getStatus());
        }
        listing.setUpdatedAt(LocalDateTime.now());

        Listing saved = listingRepository.save(listing);
        return ResponseEntity.ok(new EditListingResponse(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getPrice(),
                saved.getCategory(),
                saved.getStatus(),
                saved.getCondition(),
                saved.getImages()
        ));

    }

    @Transactional
    @DeleteMapping("/delete/{id}")
    @CacheEvict(value={"listings", "users"}, allEntries=true)
    public ResponseEntity<?> deleteListing(@PathVariable UUID id) {
        Listing listing = listingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        User owner = listing.getUser();
        owner.getListings().remove(listing);
        userRepository.save(owner);

        Iterable<User> users = userRepository.findAll();
        for (User user : users) {
            if (user.getFavourites().remove(listing)) {
                userRepository.save(user); // persist join table change
            }
        }

        listingRepository.delete(listing); // safe to delete now
        return ResponseEntity.ok("Listing successfully deleted");
    }

    @GetMapping("/all")
    public ResponseEntity<PageableListingResponse<ListingResponse>> getAllListings(
            @RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="10") int size
    ){
        Pageable pageable = PageRequest.of(page,size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(listingService.getListings(pageable));
    }


    @GetMapping("/view/{id}")
    public ResponseEntity<ListingOwnerDTO> viewListing(@PathVariable UUID id){

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found in DB"));

        ListingResponse listing = listingService.getListing(id);
        System.out.println("Listing");

        UUID sellerId = listing.getListingOwnerId();

        User listingOwner = userRepository.findById(listing.getListingOwnerId()).orElseThrow(()->new RuntimeException("Listing owner does not exist"));

        boolean chatExists = chatService.chatExists(currentUser.getId(), sellerId, id);

        Chat chat = chatService.getChat(currentUser.getId(), sellerId, id);

        UUID chatId = chat != null ? chat.getId() : null;

        ListingOwnerDTO dto = new ListingOwnerDTO(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice(),
                listing.getCategory(),
                listing.getDescription(),
                listing.getImages(),
                listing.getCondition(),
                listing.getStatus(),
                listing.getCreatedAt(),

                sellerId,
                listingOwner.getFirstName(),
                listingOwner.getLastName(),
                listingOwner.getEmail(),
                listingOwner.getProfileImage(),
                listingOwner.getLatitude(),
                listingOwner.getLongitude(),
                listingOwner.getCampusType(),

                chatExists,
                chatId
        );

        return ResponseEntity.ok().body(dto);

    }

    @GetMapping("/search")
    public ResponseEntity<?> searchListings(@RequestParam String query){
        List<Listing> listings = listingRepository.findListingsByQuery(query);
        List<ListingResponse> searchedListings = listings.stream()
                .map(listing -> new ListingResponse(
                        listing.getId(),
                        listing.getTitle(),
                        listing.getPrice(),
                        listing.getImages(),
                        listing.getCategory(),
                        listing.getCondition(),
                        listing.getDescription(),
                        listing.getStatus(),
                        listing.getUser().getId(),
                        listing.getCreatedAt()
                ))
                .toList();
        return ResponseEntity.ok().body(searchedListings);
    }

}
