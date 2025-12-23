package com.unilist.unilist.responses;

import java.util.List;

public class ListingResponse {
    private Long id;
    private String title;
    private Double price;
    private List<String> images;
    private String category;
    private String condition;
    private String description;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ListingResponse(Long id, String title, Double price, List<String> images, String category, String condition, String description) {
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
