package com.unilist.campora.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
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
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
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
