package com.krish.chatApp.model.postgres;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tenant_id", "external_id"})
})
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // Our internal UUID

    // Multi-Tenancy: A user belongs to a specific tenant
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @Column(name = "external_id", nullable = false)
    private String externalId; // The ID from the Client's system (e.g., "user_55")

    private String displayName;
    private String profileUrl;
}