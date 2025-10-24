package com.unilist.unilist.controllers;

import com.unilist.unilist.dto.listing.CreateListingDto;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.utils.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequestMapping("/listing")
@RestController
public class ListingController {

    private final ListingRepository listingRepository;

    public ListingController(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createListing(@RequestBody CreateListingDto body){
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

        return ResponseEntity.ok("Listing successfully created!");

    }
}
