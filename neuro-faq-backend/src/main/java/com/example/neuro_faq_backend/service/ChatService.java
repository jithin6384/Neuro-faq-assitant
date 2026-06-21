package com.example.neuro_faq_backend.service;

import com.example.neuro_faq_backend.model.ChatMessage;
import com.example.neuro_faq_backend.model.ChatSession;
import com.example.neuro_faq_backend.model.Role;
import com.example.neuro_faq_backend.repository.ChatMessageRepository;
import com.example.neuro_faq_backend.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final NeuroSanClient neuroSanClient;   // ← Added

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository,
                       NeuroSanClient neuroSanClient) {   // ← Added
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.neuroSanClient = neuroSanClient;
    }

    public ChatSession createSession() {
        ChatSession session = ChatSession.builder()
                .title("New Chat")
                .build();
        return chatSessionRepository.save(session);
    }
    public ChatSession getOrCreateSession(Long sessionId) {
        if (sessionId == null) {
            return createSession();
        }
        return chatSessionRepository.findById(sessionId)
                .orElseGet(this::createSession);
    }
    public List<ChatMessage> getMessages(Long sessionId) {
        return chatMessageRepository.findByChatSessionIdOrderByCreatedAtAsc(sessionId);
    }

    public ChatMessage sendMessage(Long sessionId, String content) {

        ChatSession session = getOrCreateSession(sessionId);

        if (session.getQuestionCount() >= session.getMaxQuestions()) {
            throw new RuntimeException("Maximum question limit reached");
        }

        // Save User Message
        ChatMessage userMessage = ChatMessage.builder()
                .role(Role.USER)
                .content(content)
                .chatSession(session)
                .build();
        chatMessageRepository.save(userMessage);

        session.setQuestionCount(session.getQuestionCount() + 1);

        if (session.getQuestionCount() == 1) {
            session.setTitle(generateTitle(content, session.getId()));
        }

        // Call Neuro-SAN
        Map<String, String> neuroResponse =
                neuroSanClient.callNeuroSAN(content, session.getLastChatContext());

        String botAnswer = neuroResponse.get("answer");
        String newContext = neuroResponse.get("chatContext");

        // Save Bot Message
        ChatMessage assistantMessage = ChatMessage.builder()
                .role(Role.BOT)
                .content(botAnswer)
                .chatSession(session)
                .build();

        ChatMessage savedAssistantMessage = chatMessageRepository.save(assistantMessage);

        // Update session with new context
        session.setLastChatContext(newContext);
        chatSessionRepository.save(session);

        return savedAssistantMessage;
    }

    private String generateTitle(String content, Long sessionId) {
        String baseTitle = content.length() <= 40
                ? content
                : content.substring(0, 40) + "...";

        return baseTitle + " (Session #" + sessionId + ")";
    }
}