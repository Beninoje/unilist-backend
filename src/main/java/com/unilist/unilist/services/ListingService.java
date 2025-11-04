package com.unilist.unilist.services;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.repository.ListingRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

}
