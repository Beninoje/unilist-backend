package com.unilist.campora.services;

import com.unilist.campora.dto.listing.ListingOwnerDTO;
import com.unilist.campora.model.Chat;
import com.unilist.campora.model.Listing;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ChatRepository;
import com.unilist.campora.repository.ListingRepository;
import com.unilist.campora.repository.UserRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final ListingService listingService;
    private final ChatRepository chatRepository;

    public UserService(UserRepository userRepository, EmailService emailService, ListingRepository listingRepository, ListingService listingService, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.listingService = listingService;
        this.chatRepository = chatRepository;
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
    public ListingOwnerDTO getListingOwner(UUID listingId, UUID buyerId){
        Listing listing = listingService.getListingById(listingId);
        User owner = listing.getUser();
        boolean chatExists = chatRepository.existsByBuyerIdAndSellerIdAndListingId(
                buyerId,
                owner.getId(),
                listingId
        );
        Chat chat = chatRepository.findChatByBuyerIdAndSellerIdAndListingId(
                buyerId,
                owner.getId(),
                listingId
        );
        UUID chatId = chat != null ? chat.getId() : null;
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
                owner.getEmail(),
                owner.getLatitude(),
                owner.getLongitude(),
                owner.getCampusType(),
                chatExists,
                chatId
        );
    }
    public User getListingOwnerUser(UUID listingId){
        Listing listing = listingService.getListingById(listingId);
        User owner = listing.getUser();
        return owner;
    }
}
