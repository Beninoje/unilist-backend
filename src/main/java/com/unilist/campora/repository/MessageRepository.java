package com.unilist.campora.repository;

import com.unilist.campora.model.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    @Modifying
    @Transactional
    @Query("""
            UPDATE Message m 
            SET m.read = true
            WHERE m.chat.id = :chatId
            AND m.sender.id <> :readerId
            AND m.read = false
            """)
    void markMessageAsRead(UUID chatId, UUID readerId);

    @Query(
            """
                    SELECT COUNT(m) FROM Message m
                    WHERE m.read = false
                    AND m.sender.id <> :userId
                    AND(
                        m.chat.buyer.id = :userId
                        OR m.chat.seller.id = :userId
                    )
                    """
    )
    Integer getAllUnreadMessages(UUID userId);

    @Query(
            """
                    SELECT m.chat.id, COUNT(m) FROM Message m
                    WHERE (m.chat.seller.id = :userId OR m.chat.buyer.id = :userId)
                    AND m.read = false
                    AND m.sender.id <> :userId
                    GROUP BY m.chat.id
                    """
    )
    List<Object[]> getUnreadCountsPerChat(UUID userId);
}
