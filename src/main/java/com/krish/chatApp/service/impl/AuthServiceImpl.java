package com.krish.chatApp.service.impl;

import com.krish.chatApp.dto.LoginRequest;
import com.krish.chatApp.model.postgres.Tenant;
import com.krish.chatApp.model.postgres.User;
import com.krish.chatApp.repository.postgres.TenantRepository;
import com.krish.chatApp.repository.postgres.UserRepository;
import com.krish.chatApp.service.AuthService;
import com.krish.chatApp.util.HmacUtil;
import com.krish.chatApp.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final TenantRepository tenantRepo;
    private final UserRepository userRepo;
    private final HmacUtil hmacUtil;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(TenantRepository tenantRepo, UserRepository userRepo,
            HmacUtil hmacUtil, JwtUtil jwtUtil) {
        this.tenantRepo = tenantRepo;
        this.userRepo = userRepo;
        this.hmacUtil = hmacUtil;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public String verifyAndLogin(String tenantId, String userId, String signature, LoginRequest request) {
        // 1. Fetch Tenant Secret
        Tenant tenant = tenantRepo.findByIdAndActiveTrue(tenantId)
                .orElseThrow(() -> new SecurityException("Invalid or inactive Tenant ID"));

        // 2. Validate Signature (HMAC-SHA256)
        // We hash the 'userId' using the Tenant's Secret.
        // If it matches the signature they sent, it's valid.
        String expectedSignature = hmacUtil.calculateHmac(userId, tenant.getApiSecret());

        // --- ADD THESE DEBUG LOGS ---
        System.out.println("=== HMAC DEBUG ===");
        System.out.println("Tenant Secret: " + tenant.getApiSecret());
        System.out.println("User ID: '" + userId + "'"); // Quotes help see hidden spaces
        System.out.println("Received Signature: " + signature);
        System.out.println("Calculated Signature: " + expectedSignature);
        System.out.println("==================");
        // ----------------------------

        if (!expectedSignature.equals(signature)) {
            throw new SecurityException("Invalid Signature! Integrity check failed.");
        }

        // 3. Sync User to Postgres (Upsert)
        // If user exists, update name. If not, create them.
        Optional<User> existingUser = userRepo.findByTenantIdAndExternalId(tenantId, userId);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            // Update profile if provided
            if (request.displayName() != null) user.setDisplayName(request.displayName());
            if (request.profileUrl() != null) user.setProfileUrl(request.profileUrl());
        } else {
            user = new User();
            user.setTenant(tenant);
            user.setExternalId(userId);
            user.setDisplayName(request.displayName() != null ? request.displayName() : "User " + userId);
            user.setProfileUrl(request.profileUrl());
        }
        userRepo.save(user);

        // 4. Generate Session Token (JWT)
        return jwtUtil.generateToken(user.getExternalId(), tenantId);
    }
}