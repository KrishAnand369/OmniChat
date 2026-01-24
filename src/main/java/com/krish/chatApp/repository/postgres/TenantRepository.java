package com.krish.chatApp.repository.postgres;

import com.krish.chatApp.model.postgres.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TenantRepository extends JpaRepository<Tenant, String> {
    // Find by ID is built-in, but we might want this:
    Optional<Tenant> findByIdAndActiveTrue(String id);
}