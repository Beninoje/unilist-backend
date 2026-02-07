package com.unilist.unilist.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @ManyToOne
    @JoinColumn(name="buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name="seller_id", nullable = false)
    private User seller;

    @Column(name="listing_id")
    private UUID listingId;

    @OneToMany(mappedBy="chat", cascade = CascadeType.ALL)
    private List<Message> messages;

    @Column(name="created_at")
    private Instant createdAt;

    public boolean hasUser(UUID userId){
        return (userId.equals(buyer.getId()) || userId.equals(seller.getId()));
    }


}
