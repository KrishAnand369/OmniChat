package com.krish.chatApp.repository.postgres;

import com.krish.chatApp.model.postgres.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    // Efficiently fetch all User IDs in a specific conversation
    @Query("SELECT p.user.externalId FROM Participant p WHERE p.conversation.id = :conversationId")
    List<String> findUserIdsByConversationId(@Param("conversationId") UUID conversationId);
}