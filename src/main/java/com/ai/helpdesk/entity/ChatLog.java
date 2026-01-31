package com.ai.helpdesk.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;

    @Column(length = 5000) // Allow long messages
    private String message;

    private String role; // "USER" or "ASSISTANT"
    private LocalDateTime timestamp;

    public ChatLog(String userId, String message, String role) {
        this.userId = userId;
        this.message = message;
        this.role = role;
        this.timestamp = LocalDateTime.now();
    }

    public ChatLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}