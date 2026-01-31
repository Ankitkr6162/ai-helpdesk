package com.ai.helpdesk.service;


import com.ai.helpdesk.entity.ChatLog;
import com.ai.helpdesk.repository.ChatLogRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiHelpDeskService {

    private final ChatClient chatClient;
    private final ChatLogRepository chatLogRepository;

    public AiHelpDeskService(ChatClient.Builder builder, ChatLogRepository chatLogRepository) {
        this.chatLogRepository = chatLogRepository;

        this.chatClient = builder
                .defaultSystem("You are an IT support agent. " +
                        "You have access to tools 'createTicket' and 'getTicketStatus'. " +
                        "When calling 'createTicket', the argument 'issue' must be a simple string, NOT a JSON object. " +
                        "Example: {\"issue\": \"printer broken\"}. " +
                        "Do not wrap the description in another object.")
                .build();
    }

    public String chat(String userId, String userMessage) {
        // 1. Load History
        List<Message> history = loadHistory(userId);

        // 2. Call AI
        // We do NOT save the user message to DB yet; we wait for success.
        System.out.println("User (" + userId + "): " + userMessage);

        String response = chatClient.prompt()
                .messages(history)          // Context
                .user(userMessage)          // Current input
                .functions("createTicket", "getTicketStatus") // Explicitly enable tools
                .call()
                .content();

        // 3. Save Interaction (User + Assistant)
        saveInteraction(userId, userMessage, response);

        return response;
    }

    // Helper: Load history from MySQL
    private List<Message> loadHistory(String userId) {
        return chatLogRepository.findByUserIdOrderByTimestampAsc(userId).stream()
                .map(log -> {
                    if ("USER".equals(log.getRole())) {
                        return new UserMessage(log.getMessage());
                    } else {
                        return new AssistantMessage(log.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }

    // Helper: Save history to MySQL
    private void saveInteraction(String userId, String input, String output) {
        ChatLog userLog = new ChatLog(userId, input, "USER");
        chatLogRepository.save(userLog);

        ChatLog botLog = new ChatLog(userId, output, "ASSISTANT");
        chatLogRepository.save(botLog);
    }
}