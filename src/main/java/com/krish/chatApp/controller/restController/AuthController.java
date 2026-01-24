package com.krish.chatApp.controller.restController;

import com.krish.chatApp.dto.LoginRequest;
import com.krish.chatApp.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    // Inject the value dynamically based on the active profile
    @Value("${app.socket-url}")
    private String socketUrl;

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestHeader("X-Tenant-ID") String tenantId,
            @RequestHeader("X-User-ID") String userId,
            @RequestHeader("X-Auth-Signature") String signature,
            @RequestBody(required = false) LoginRequest request) { // Body is optional

        // Handle null body safely
        System.out.println(socketUrl);
        if (request == null) {
            request = new LoginRequest(null, null);
        }

        String token = authService.verifyAndLogin(tenantId, userId, signature, request);

        // Return the JWT and the connection URL for WebSockets
        return ResponseEntity.ok(Map.of(
                "token", token,
                "socketUrl", socketUrl
        ));
    }
}