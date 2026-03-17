package com.unilist.campora.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="messages")
@Builder
public class Message {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @ManyToOne
    @JoinColumn(name="chat_id", nullable = false)
    private Chat chat;

    @ManyToOne
    @JoinColumn(name="sender_id",nullable = false)
    @JsonBackReference
    private User sender;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name="reply_to_message_id")
    private Message replyTo;

    private Boolean isInitial = false;

    @Column(name="read",nullable = false)
    private Boolean read = false;

    @Column(nullable = false)
    private Instant createdAt;
}
