package com.unilist.campora.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserResponse {
    private String firstName;
    private String lastName;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private String campusType;
    private String profileImage;

}
