package com.unilist.campora.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
@Table(name="chats",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"seller_id", "buyer_id", "listing_id"}
        )
)
@JsonPropertyOrder({ "id", "buyer_id", "seller_id", "listing_id", "messages", "createdAt" })
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
    @JsonManagedReference
    private List<Message> messages;

    @Column(name="created_at")
    private Instant createdAt;

    public boolean hasUser(UUID userId){
        return (userId.equals(buyer.getId()) || userId.equals(seller.getId()));
    }


}
