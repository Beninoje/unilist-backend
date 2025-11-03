package com.unilist.unilist.controllers;

import com.unilist.unilist.dto.listing.CreateListingDto;
import com.unilist.unilist.dto.listing.EditListingDto;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.services.ListingService;
import com.unilist.unilist.utils.SecurityUtils;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequestMapping("/listing")
@RestController
public class ListingController {

    private final ListingRepository listingRepository;
    private final ListingService listingService;

    public ListingController(ListingRepository listingRepository, ListingService listingService) {
        this.listingRepository = listingRepository;
        this.listingService = listingService;
    }

    @PostMapping("/create")
    public ResponseEntity<List<Listing>> createListing(@RequestBody CreateListingDto body){
        User currentUser = SecurityUtils.getCurrentUser();

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
    public ResponseEntity<?> editListing(@PathVariable Long id, @RequestBody EditListingDto body){
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
        listing.setUpdatedAt(LocalDateTime.now());

        listingRepository.save(listing);
        return ResponseEntity.ok("Listing successfully updated");

    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteListing(@PathVariable Long id){
        Optional<Listing> currentListing = listingRepository.findById(id);
        Listing listing = currentListing.orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found")
        );
        listingRepository.delete(listing);
        return ResponseEntity.ok("Listing successfully deleted");

    }

    @GetMapping("/all")
    public ResponseEntity<List<Listing>> getAllListings(){
        List<Listing> allListings = listingService.getAllListings();
        return ResponseEntity.ok(allListings);
    }

}
