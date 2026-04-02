package com.unilist.campora.dto.listing;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListingOwnerDTO {
    UUID listingId;
    String title;
    Double price;
    String category;
    String description;
    List<String> images;
    String condition;
    String status;
    LocalDateTime createdAt;
    UUID ownerId;
    String ownerFirstName;
    String ownerLastName;
    String ownerEmail;
    String ownerProfileImage;
    Double latitude;
    Double longitude;
    String campusType;
    boolean existingChat;
    UUID chatId;



}
