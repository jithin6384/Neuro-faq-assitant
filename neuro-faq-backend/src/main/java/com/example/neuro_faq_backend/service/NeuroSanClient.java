package com.example.neuro_faq_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class NeuroSanClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${neuro-san.url:http://localhost:4173}")
    private String neuroSanUrl;

    public Map<String, String> callNeuroSAN(String userMessage, String lastChatContext) {

        String url = neuroSanUrl + "/api/v1/faq_assistant/streaming_chat";

        Map<String, Object> payload = new HashMap<>();
        payload.put("user_message", Map.of("text", userMessage));

        if (lastChatContext != null && !lastChatContext.isBlank()) {
            try {
                payload.put("chat_context", objectMapper.readTree(lastChatContext));
            } catch (Exception e) {
                payload.put("chat_context", lastChatContext);
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            String responseBody = restTemplate.postForObject(url, request, String.class);

            if (responseBody == null || responseBody.isBlank()) {
                Map<String, String> emptyResult = new HashMap<>();
                emptyResult.put("answer", "Sorry, I couldn't process your request.");
                emptyResult.put("chatContext", null);
                return emptyResult;
            }

            String finalAnswer = "Sorry, I couldn't process your request.";
            String newChatContext = null;

            for (String line : responseBody.split("\n")) {
                if (line.trim().isEmpty()) continue;

                try {
                    JsonNode node = objectMapper.readTree(line);
                    JsonNode resp = node.path("response");

                    String type = resp.path("type").asText();

                    if ("AGENT_FRAMEWORK".equals(type)) {

                        JsonNode textNode = resp.path("text");
                        if (!textNode.isMissingNode() && !textNode.isNull()) {
                            finalAnswer = textNode.asText();
                        }

                        JsonNode contextNode = resp.path("chat_context");
                        if (!contextNode.isMissingNode() && !contextNode.isNull()) {
                            newChatContext = objectMapper.writeValueAsString(contextNode);
                        }
                    }

                } catch (Exception ignored) {
                }
            }

            Map<String, String> result = new HashMap<>();
            result.put("answer", finalAnswer);
            result.put("chatContext", newChatContext);
            return result;

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, String> error = new HashMap<>();
            error.put("answer", "Sorry, Neuro-SAN service is not responding. Please try again.");
            error.put("chatContext", null);
            return error;
        }
    }
}