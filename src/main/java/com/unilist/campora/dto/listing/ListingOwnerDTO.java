package com.unilist.campora.dto.listing;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ListingOwnerDTO {
    UUID listingId;
    String title;
    Double price;
    String category;
    String description;
    List<String> images;
    String condition;
    LocalDateTime createdAt;
    UUID ownerId;
    String ownerFirstName;
    String ownerLastName;
    String ownerEmail;

    public ListingOwnerDTO(UUID listingId, String title, Double price, String category, String description, List<String> images, String condition, LocalDateTime createdAt, UUID ownerId, String ownerFirstName, String ownerLastName, String ownerEmail) {
        this.listingId = listingId;
        this.title = title;
        this.price = price;
        this.category = category;
        this.description = description;
        this.images = images;
        this.condition = condition;
        this.createdAt = createdAt;
        this.ownerId = ownerId;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerEmail = ownerEmail;
    }

    public ListingOwnerDTO() {}

}
