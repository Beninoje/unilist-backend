package com.unilist.campora.responses.users;

import com.unilist.campora.model.Listing;
import com.unilist.campora.responses.listings.ListingResponse;
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
public class ViewUserResponse {
    private UUID id;
    String email;
    String firstName;
    String lastName;
    List<ListingResponse> listings;
    String campusType;
    String profileImage;
    boolean enabled;
}
