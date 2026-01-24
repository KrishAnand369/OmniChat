package com.krish.chatApp.dto;

// The headers handle the ID/Security.
// This body is just for updating profile info (Upsert).
public record LoginRequest(
        String displayName,
        String profileUrl
) {}
