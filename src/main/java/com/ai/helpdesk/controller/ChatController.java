package com.ai.helpdesk.controller;


import com.ai.helpdesk.service.AiHelpDeskService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiHelpDeskService aiService;

    public ChatController(AiHelpDeskService aiService) {
        this.aiService = aiService;
    }

    // Endpoint: POST /api/chat?userId=john_doe
    // Body: { "message": "My printer is broken" }
    @PostMapping
    public Map<String, String> chat(@RequestParam String userId, @RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        String response = aiService.chat(userId, userMessage);
        return Map.of("response", response);
    }
}
