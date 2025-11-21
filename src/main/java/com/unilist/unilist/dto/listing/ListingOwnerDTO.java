package com.unilist.unilist.dto.listing;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.unilist.unilist.model.Listing;

import java.time.LocalDateTime;
import java.util.List;



public class ListingOwnerDTO {
    Long listingId;
    String title;
    Double price;
    String category;
    String description;
    List<String> images;
    String condition;
    LocalDateTime createdAt;
    String ownerFirstName;
    String ownerLastName;
    String ownerEmail;

    public ListingOwnerDTO(Long listingId, String title, Double price, String category, String description, List<String> images, String condition, LocalDateTime createdAt, String ownerFirstName, String ownerLastName, String ownerEmail) {
        this.listingId = listingId;
        this.title = title;
        this.price = price;
        this.category = category;
        this.description = description;
        this.images = images;
        this.condition = condition;
        this.createdAt = createdAt;
        this.ownerFirstName = ownerFirstName;
        this.ownerLastName = ownerLastName;
        this.ownerEmail = ownerEmail;
    }

    public ListingOwnerDTO() {}

    public Long getListingId() {
        return listingId;
    }

    public void setListingId(Long listingId) {
        this.listingId = listingId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getOwnerFirstName() {
        return ownerFirstName;
    }

    public void setOwnerFirstName(String ownerFirstName) {
        this.ownerFirstName = ownerFirstName;
    }

    public String getOwnerLastName() {
        return ownerLastName;
    }

    public void setOwnerLastName(String ownerLastName) {
        this.ownerLastName = ownerLastName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }
}
