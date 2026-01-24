package com.krish.chatApp.repository.postgres;

import com.krish.chatApp.model.postgres.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {
    // It tells JPA: "Find a User where tenant.id = ?1 AND externalId = ?2"
    Optional<User> findByTenantIdAndExternalId(String tenantId, String externalId);
}