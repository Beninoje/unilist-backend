package com.unilist.campora.services;

import com.unilist.campora.model.Listing;
import com.unilist.campora.model.User;
import com.unilist.campora.repository.ListingRepository;
import com.unilist.campora.repository.UserRepository;
import com.unilist.campora.responses.ListingResponse;
import com.unilist.campora.responses.PageableListingResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final UserRepository userRepository;


    public ListingService(ListingRepository listingRepository, UserRepository userRepository) {
        this.listingRepository = listingRepository;
        this.userRepository = userRepository;
    }

    @Cacheable(
            value="listings",
            key="#pageable.pageNumber + '-' + #pageable.pageSize"
    )
    public PageableListingResponse<ListingResponse> getListings(Pageable pageable){

        Page<Listing> page = listingRepository.findAll(pageable);
        Page<ListingResponse> mapped = page.map(
        listing -> new ListingResponse(
                listing.getId(),
                listing.getTitle(),
                listing.getPrice(),
                listing.getImages(),
                listing.getCategory(),
                listing.getCondition(),
                listing.getDescription(),
                listing.getUser().getId()

        ));
        return new PageableListingResponse<>(mapped);

    }

    public Listing getListingById(UUID id){
        return listingRepository.findById(id)
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Listing not found"));
    }

}
