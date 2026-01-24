package com.krish.chatApp.model.postgres;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "tenants")
@Data
public class Tenant {
    @Id
    private String id; // e.g., "food_delivery_app" (Manually set)

    @Column(nullable = false)
    private String apiSecret; // The secret key used for HMAC signatures

    @Column(nullable = false)
    private String displayName; // "Food Delivery Inc."

    private boolean active = true;
}