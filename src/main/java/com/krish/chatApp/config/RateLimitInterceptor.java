package com.krish.chatApp.config;

import com.krish.chatApp.service.RateLimiterService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
public class RateLimitInterceptor implements ChannelInterceptor {

    private final RateLimiterService rateLimiter;

    public RateLimitInterceptor(RateLimiterService rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // Only check limits when a user tries to SEND a message
        if (StompCommand.SEND.equals(accessor.getCommand())) {

            // 1. Extract Tenant ID (We stored this in the session during Connect)
            // Note: You must ensure Tenant ID is put into session attributes during Handshake!
            String tenantId = (String) accessor.getSessionAttributes().get("tenant_id");

            if (tenantId != null) {
                // 2. Check Redis
                if (!rateLimiter.isAllowed(tenantId)) {
                    throw new IllegalArgumentException("Rate limit exceeded for tenant: " + tenantId);
                }
            }
        }
        return message;
    }
}