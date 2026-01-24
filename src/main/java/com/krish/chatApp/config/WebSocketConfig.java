package com.krish.chatApp.config;

import com.krish.chatApp.util.JwtUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    // Inject JwtUtil so we can verify tokens
    public WebSocketConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*") // Allow Localhost/File connections
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Use Simple Broker (In-Memory) for now to eliminate RabbitMQ complexity
        // We will switch back to RabbitMQ once the connection works!
        registry.enableSimpleBroker("/topic", "/queue");
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // 1. Check if this is a CONNECT frame
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {

                    // 2. Extract the "Authorization" header
                    List<String> authorization = accessor.getNativeHeader("Authorization");

                    if (authorization != null && !authorization.isEmpty()) {
                        String token = authorization.get(0).replace("Bearer ", "");

                        try {
                            // 3. Validate Token & Get User ID
                            var claims = jwtUtil.validateToken(token);
                            String userId = claims.getSubject();

                            // 4. Create Auth Object (This fixes "User not authenticated" error)
                            UsernamePasswordAuthenticationToken user =
                                    new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

                            accessor.setUser(user);
                            System.out.println("✅ WebSocket Authenticated User: " + userId);

                        } catch (Exception e) {
                            System.out.println("❌ WebSocket Auth Failed: " + e.getMessage());
                        }
                    }
                }
                return message;
            }
        });
    }
}