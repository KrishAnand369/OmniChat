package com.krish.chatApp.model.postgres;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "conversations")
@Data
public class Conversation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    private String name; // Null for 1-on-1, "Team Alpha" for Groups

    @Column(nullable = false)
    private boolean isGroup; // true = Group, false = Direct Message

    private String description; // "Official Project Alpha Channel"

    private String avatarUrl; // Group Icon

    private Instant createdAt = Instant.now();
}