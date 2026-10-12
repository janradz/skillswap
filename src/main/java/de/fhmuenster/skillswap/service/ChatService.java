package de.fhmuenster.skillswap.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;
import org.springframework.security.access.prepost.PreAuthorize;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String chatWithAi(String userMessage, String userId) {
        return this.chatClient.prompt()
                .user(userMessage)
                // Hier passiert die Magie: Das Chat-Memory wird genau diesem User (Keycloak ID) zugeordnet!
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, userId))
                .call()
                .content();
    }
}
