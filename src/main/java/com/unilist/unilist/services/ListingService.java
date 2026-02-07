package com.unilist.unilist.services;

import com.unilist.unilist.model.Listing;
import com.unilist.unilist.model.User;
import com.unilist.unilist.repository.ListingRepository;
import com.unilist.unilist.responses.ListingOwnerResponse;
import com.unilist.unilist.responses.ListingResponse;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;


    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Page<ListingResponse> getListings(Pageable pageable){
        return listingRepository.findAll(pageable)
                .map(listing -> new ListingResponse(
                        listing.getId(),
                        listing.getTitle(),
                        listing.getPrice(),
                        listing.getImages(),
                        listing.getCategory(),
                        listing.getCondition(),
                        listing.getDescription()
                ));
    }

    public Listing getListingById(UUID id){
        return listingRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Listing not found"));
    }

}
