package com.unilist.campora.responses.listings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EditListingResponse {
    private UUID id;
    private String title;
    private String description;
    private Double price;
    private String category;
    private String status;
    private String condition;
    private List<String> images;
}
