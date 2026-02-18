package com.unilist.unilist.services;

import com.unilist.unilist.dto.listing.ListingOwnerDTO;
import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;

    public UserService(UserRepository userRepository, EmailService emailService, ListingRepository listingRepository, ListingService listingService) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.listingService = listingService;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        return users;
    }

    public Set<Listing> getUserFavourites(UUID userId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("Not found"));
        return user.getFavourites();
    }

    @Cacheable(value="listing_owner", key="#listingId")
    public ListingOwnerDTO getListingOwner(UUID listingId){
        Listing listing = listingService.getListingById(listingId);
        User owner = listing.getUser();

        return new ListingOwnerDTO(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice(),
                listing.getCategory(),
                listing.getDescription(),
                listing.getImages(),
                listing.getCondition(),
                listing.getCreatedAt(),
                owner.getId(),
                owner.getFirstName(),
                owner.getLastName(),
                owner.getEmail()
        );
    }
    public User getListingOwnerUser(UUID listingId){
        Listing listing = listingService.getListingById(listingId);
        User owner = listing.getUser();
        return owner;
    }
}
