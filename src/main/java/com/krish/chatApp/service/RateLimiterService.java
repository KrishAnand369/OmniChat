package com.krish.chatApp.service;

public interface RateLimiterService {
    boolean isAllowed(String tenantId);
}
