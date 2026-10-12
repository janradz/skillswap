package de.fhmuenster.skillswap.controller;

import de.fhmuenster.skillswap.service.ChatService;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping
    public String chat(@RequestParam String message, Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("Unauthorized");
        }
        
        String userId;
        if (authentication.getPrincipal() instanceof Jwt) {
            userId = ((Jwt) authentication.getPrincipal()).getSubject();
        } else {
            // Für Gastzugriffe oder andere Authentifizierungsmethoden
            userId = authentication.getName(); 
        }
        
        // Den ChatService aufrufen und die Nachricht sowie die extrahierte User-ID übergeben
        return chatService.chatWithAi(message, userId);
    }
}
