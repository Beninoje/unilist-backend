package com.unilist.campora.responses;

import java.util.List;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String email,
        String firstName,
        String lastName,
        List<UUID> favourites,
        List<UUID> listings,
        boolean onboardingComplete,
        Double latitude,
        Double longitude,
        String postalCode,
        String campusType
        ) {

}

