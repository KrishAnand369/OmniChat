package com.krish.chatApp.repository.postgres;

import com.krish.chatApp.model.postgres.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ConversationRepository extends JpaRepository<Conversation, Long> {

}