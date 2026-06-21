package com.example.neuro_faq_backend.controller;

import com.example.neuro_faq_backend.model.ChatMessage;
import com.example.neuro_faq_backend.model.ChatSession;
import com.example.neuro_faq_backend.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ChatMessage sendMessage(@RequestBody Map<String, Object> payload) {

        String sessionIdStr = (String) payload.get("sessionId");
        Long sessionId = Long.valueOf(sessionIdStr);   // Convert to Long
        String content = (String) payload.get("text");

        return chatService.sendMessage(sessionId, content);
    }
    @GetMapping("/chat/history")
    public Map<String, Object> getOrCreateHistory(@RequestParam(required = false) Long sessionId) {
        ChatSession session;

        if (sessionId == null) {
            session = chatService.createSession(); // First time
        } else {
            session = chatService.getOrCreateSession(sessionId);
        }

        List<ChatMessage> messages = chatService.getMessages(session.getId());

        return Map.of(
                "sessionId", session.getId(),
                "messages", messages,
                "title", session.getTitle()
        );
    }

    @PostMapping("/sessions")
    public ChatSession createSession() {
        return chatService.createSession();
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public List<ChatMessage> getMessages(@PathVariable Long sessionId) {
        return chatService.getMessages(sessionId);
    }
}