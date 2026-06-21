package com.example.neuro_faq_backend.repository;

import com.example.neuro_faq_backend.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
}