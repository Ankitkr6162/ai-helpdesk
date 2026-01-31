package com.ai.helpdesk.repository;

import com.ai.helpdesk.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {
    List<ChatLog> findByUserIdOrderByTimestampAsc(String userId);
}
