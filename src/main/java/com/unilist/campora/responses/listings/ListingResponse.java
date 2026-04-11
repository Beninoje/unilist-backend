package com.unilist.campora.responses.listings;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListingResponse {
    private UUID id;
    private String title;
    private Double price;
    private String category;
    private String status;
    private String condition;
    private List<String> images;
    private LocalDateTime createdAt;
}
