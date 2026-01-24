package com.krish.chatApp.service;

import com.krish.chatApp.dto.LoginRequest;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    public String verifyAndLogin(String tenantId, String userId, String signature, LoginRequest request);
}