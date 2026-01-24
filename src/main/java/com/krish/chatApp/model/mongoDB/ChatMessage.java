package com.krish.chatApp.model.mongoDB;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.UUID;

@Document(collection = "messages")
@Data
public class ChatMessage {

    @Id
    private String id; // Mongo ID

    // Link to Postgres
    @Indexed
    private UUID conversationId;

    // Link to Postgres User
    private UUID senderId;

    private String content; // "Hello World"

    private MessageType type = MessageType.TEXT;

    @Indexed
    private Instant timestamp = Instant.now();

    public enum MessageType {
        TEXT, IMAGE, FILE, SYSTEM
    }
}
