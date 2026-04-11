package com.unilist.campora.repository;

import com.unilist.campora.model.Listing;
import com.unilist.campora.responses.ListingResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ListingRepository extends JpaRepository<Listing, UUID> {

    List<Listing> findByUserId(UUID id);

    @Query("SELECT l from Listing l WHERE "
    + "LOWER(l.title) LIKE LOWER(CONCAT('%',:query, '%')) OR "
    + "LOWER(l.category) LIKE LOWER(CONCAT('%',:query, '%'))")
    List<Listing> findListingsByQuery(String query);

    List<Listing> findListingsByCategory(String query);

    @Query("SELECT l FROM Listing l JOIN FETCH l.user WHERE l.id = :id")
    Optional<Listing> findByIdWithUser(@Param("id") UUID id);
}
