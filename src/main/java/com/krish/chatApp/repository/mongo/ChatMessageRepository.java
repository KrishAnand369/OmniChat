package com.krish.chatApp.repository.mongo;

import com.krish.chatApp.model.mongoDB.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    // Fetch history for a room (e.g., "Give me the last 50 messages")
    // We use UUID for conversationId because that's how it's stored in Postgres
    List<ChatMessage> findByConversationIdOrderByTimestampDesc(UUID conversationId, Pageable pageable);
}