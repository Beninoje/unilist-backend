package com.unilist.campora.responses;

import lombok.Getter;
import lombok.Setter;

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


    public ListingResponse(UUID id, String title, Double price, List<String> images, String category, String condition, String description) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.images = images;
        this.category = category;
        this.condition = condition;
        this.description = description;
    }

    public ListingResponse() {

    }
}
