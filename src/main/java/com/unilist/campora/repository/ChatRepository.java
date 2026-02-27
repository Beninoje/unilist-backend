package com.unilist.campora.repository;

import com.unilist.campora.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ChatRepository  extends JpaRepository<Chat, UUID> {
    boolean existsByBuyerIdAndSellerIdAndListingId(
            UUID buyerId,
            UUID sellerId,
            UUID listingId
    );
}
