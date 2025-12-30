package com.unilist.unilist.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@Table(name="chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="buyer_id", nullable = false)
    private User buyer;

    @ManyToOne
    @JoinColumn(name="seller_id", nullable = false)
    private User seller;

    @Column(name="listing_id")
    private Long listingId;

    @OneToMany(mappedBy="chat", cascade = CascadeType.ALL)
    private List<Message> messages;

    @Column(name="created_at")
    private Instant createdAt;

    public boolean hasUser(Long userId){
        return (userId.equals(buyer.getId()) || userId.equals(seller.getId()));
    }


}
