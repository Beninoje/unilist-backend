package com.unilist.campora.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unilist.campora.utils.enums.ReportReason;
import com.unilist.campora.utils.enums.ReportStatus;
import com.unilist.campora.utils.enums.ReportTargetType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="reports")
public class Report {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name="target_type", nullable = false)
    private ReportTargetType targetType;

    @Column(name="reporter_id", nullable = false)
    private UUID reporterId;

    @Column(name="target_id", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING)
    @Column(name="reason", nullable = false)
    private ReportReason reason;

    @Column(name="description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private ReportStatus status;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    @JsonIgnore
    @JsonBackReference
    private User user;
}
