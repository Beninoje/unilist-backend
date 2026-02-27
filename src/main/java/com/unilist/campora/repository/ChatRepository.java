package com.unilist.campora.repository;

import com.unilist.campora.model.Chat;
import com.unilist.campora.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRepository  extends JpaRepository<Chat, UUID> {
    boolean existsByBuyerIdAndSellerIdAndListingId(
            UUID buyerId,
            UUID sellerId,
            UUID listingId
    );
    @Query("SELECT c FROM Chat c WHERE c.buyer = :user OR c.seller = :user")
    List<Chat> findAllByBuyerOrSeller(User user);
}
