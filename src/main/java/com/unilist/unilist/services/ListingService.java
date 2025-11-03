package com.unilist.unilist.services;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ListingService {

    private final ListingRepository listingRepository;


    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public List<Listing> getAllListings(){
        List<Listing> listings = new ArrayList<>();
        listingRepository.findAll().forEach(listings::add);
        return listings;
    }

}
