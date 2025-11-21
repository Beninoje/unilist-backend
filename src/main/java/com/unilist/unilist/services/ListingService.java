package com.unilist.unilist.services;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.responses.ListingOwnerResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ListingService {

    private final ListingRepository listingRepository;


    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    @Cacheable("listings")
    public List<Listing> getAllListings(){
        System.out.println("Fetching from DB...");
        return (List<Listing>) listingRepository.findAll();

    }
    @Cacheable(value="listings", key="#id")
    public Listing getListingById(Long id){
        return listingRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Listing not found"));
    }

}
