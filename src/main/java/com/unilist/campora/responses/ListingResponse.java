package com.unilist.campora.responses;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ListingResponse {
    private UUID id;
    private String title;
    private Double price;
    private List<String> images;
    private String category;
    private String condition;
    private String description;
    private String status;
    private UUID listingOwnerId;
    private LocalDateTime createdAt;


    public ListingResponse(UUID id, String title, Double price, List<String> images, String category, String condition,String status, String description,UUID listingOwnerId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.images = images;
        this.category = category;
        this.condition = condition;
        this.status = status;
        this.description = description;
        this.listingOwnerId = listingOwnerId;
        this.createdAt = createdAt;

    }

    public ListingResponse() {

    }
}
