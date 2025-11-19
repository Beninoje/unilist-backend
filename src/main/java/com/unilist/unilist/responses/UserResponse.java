package com.unilist.unilist.responses;

import java.util.List;

public record UserResponse(
        Long id,
        String email,
        String password,
        String firstName,
        String lastName,
        List<Long> favourites,
        List<Long> listings
        ) {

}

