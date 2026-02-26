package com.unilist.campora.responses;

import java.time.LocalDateTime;
import java.util.List;

public record ListingOwnerResponse (
    Long listingId,
    String title,
    Double price,
    String category,
    String description,
    List<String> images,
    String condition,
    LocalDateTime createdAt,
    String ownerFirstName,
    String ownerLastName,
    String ownerEmail
) {

//    public ListingOwnerResponse(Listing listing, ListingOwnerDTO owner) {
//        this(
//                listing.getId(),
//                listing.getTitle(),
//                listing.getPrice(),
//                listing.getCategory(),
//                listing.getDescription(),
//                listing.getImages(),
//                listing.getCondition(),
//                listing.getCreatedAt(),
//                owner.firstName(),
//                owner.lastName(),
//                owner.email()
//        );
//    }


}





