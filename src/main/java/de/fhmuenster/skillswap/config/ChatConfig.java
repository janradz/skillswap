package de.fhmuenster.skillswap.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {
    
    @Bean
    public ChatMemory chatMemory() {
        // In neueren Spring AI Versionen (wie 1.1.5) nutzt man den Repository-Ansatz
        // in Kombination mit dem MessageWindowChatMemory für das Limit
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(new InMemoryChatMemoryRepository())
                .maxMessages(100)
                .build();
    }
}
