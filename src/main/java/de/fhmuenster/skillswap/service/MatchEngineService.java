package de.fhmuenster.skillswap.service;

import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Offer;
import de.fhmuenster.skillswap.repository.EnquiryRepository;
import de.fhmuenster.skillswap.repository.MatchRepository;
import de.fhmuenster.skillswap.repository.OfferRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.StructuredOutputValidationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import de.fhmuenster.skillswap.dto.MatchEvaluationFeedbackDTO;
import de.fhmuenster.skillswap.dto.SemanticMatchResultDTO;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchEngineService {

    private final OfferRepository offerRepository;
    private final EnquiryRepository enquiryRepository;
    private final MatchRepository matchRepository;
    private final MatchService matchService;
    private final ChatClient chatClient;

    public MatchEngineService(OfferRepository offerRepository,
            EnquiryRepository enquiryRepository,
            MatchRepository matchRepository,
            MatchService matchService,
            ChatClient.Builder chatClientBuilder) {
        this.offerRepository = offerRepository;
        this.enquiryRepository = enquiryRepository;
        this.matchRepository = matchRepository;
        this.matchService = matchService;
        this.chatClient = chatClientBuilder.build();
    }

    @Scheduled(fixedDelay = 30000)
    public void runMatchingRoutine() {
        List<Offer> allOffers = offerRepository.findAll();
        List<Enquiry> allEnquiries = enquiryRepository.findAll();

        for (Offer offer : allOffers) {
            for (Enquiry enquiry : allEnquiries) {

                // user darf nicht mit sich selbst gematcht werden
                if (offer.getUser().getId().equals(enquiry.getUser().getId())) {
                    continue;
                }

                // match existiert bereits?
                if (matchRepository.existsByOfferAndEnquiry(offer, enquiry)) {
                    continue;
                }

                // gemeinsamer termin?
                LocalDateTime gemeinsamerTermin = findeGemeinsamenTermin(offer, enquiry);

                if (gemeinsamerTermin != null) {
                    int score = calculateSemanticScore(offer, enquiry);

                    if (score >= 50) {
                        matchService.createAutomaticMatch(offer, enquiry, score, gemeinsamerTermin);
                        System.out.println("✅ ERFOLG: Match erstellt in der Datenbank! Score: " + score);
                    } else {
                        System.out.println("❌ ABGELEHNT: Score ist zu niedrig für ein Match (" + score + " < 50). Es wird nicht gespeichert.");
                    }
                }
            }
        }
    }

    private LocalDateTime findeGemeinsamenTermin(Offer offer, Enquiry enquiry) {
        List<LocalDateTime> anbieterTermine = offer.getUser().getVerfuegbareTermine();
        List<LocalDateTime> sucherTermine = enquiry.getUser().getVerfuegbareTermine();

        if (anbieterTermine == null || sucherTermine == null) {
            return null;
        }

        // ersten ueberschneidenden termin finden
        for (LocalDateTime termin : anbieterTermine) {
            if (sucherTermine.contains(termin)) {
                return termin;
            }
        }

        return null;
    }

    private int calculateSemanticScore(Offer offer, Enquiry enquiry) {
        String systemPrompt = "Du bist ein Matching-Algorithmus für eine SkillSwap-Plattform. " +
                "Vergleiche das Angebot (Offer) mit dem Gesuch (Enquiry). " +
                "Kriterien für ein gutes Match (Score >= 50): Das fachliche Thema stimmt grundlegend überein (z.B. beides Mathe, beides Java). " +
                "Hinweis: Sei wohlwollend. Auch kurze oder unvollständige Beschreibungen rechtfertigen einen hohen Score, wenn das Fachgebiet passt. " +
                "Vergib einen Score zwischen 0 und 100, wobei 100 ein perfektes Match ist. " +
                "WICHTIG: Antworte STRIKT im JSON Format mit exakt zwei Feldern: 'score' (als Integer) und 'reason' (als kurzer Text mit der Begründung).";

        String baseUserPrompt = String.format(
                "Angebot Titel: %s\nAngebot Beschreibung: %s\n\nGesuch Titel: %s\nGesuch Beschreibung: %s",
                offer.getTitel(), offer.getBeschreibung(),
                enquiry.getTitel(), enquiry.getBeschreibung());

        var validationAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(SemanticMatchResultDTO.class)
                .maxRepeatAttempts(3)
                .advisorOrder(Advisor.HIGHEST_PRECEDENCE + 1000)
                .build();

        var feedbackAdvisor = StructuredOutputValidationAdvisor.builder()
                .outputType(MatchEvaluationFeedbackDTO.class)
                .maxRepeatAttempts(3)
                .advisorOrder(Advisor.HIGHEST_PRECEDENCE + 1001)
                .build();

        String previousFeedback = "";

        for (int i = 0; i < 3; i++) {
            try {
                // 1. GENERATOR
                String genPrompt = baseUserPrompt
                        + (previousFeedback.isEmpty() ? "" : "\n\nKritik vom Evaluator: " + previousFeedback);
                SemanticMatchResultDTO result = chatClient.prompt()
                        .system(systemPrompt)
                        .user(genPrompt)
                        .advisors(validationAdvisor)
                        .call()
                        .entity(SemanticMatchResultDTO.class);

                if (result == null) {
                    return 0; // Fallback falls unerwartet null
                }

                System.out.println("GENERATOR: Score " + result.score() + " | Reason: " + result.reason());

                // 2. EVALUATOR
                String evalPrompt = String.format(
                        "Prüfe dieses generierte Match-Ergebnis:\nGenerierter Score: %d\nGenerierte Begründung: %s\n\nBasierend auf diesen echten Daten:\n%s",
                        result.score(), result.reason(), baseUserPrompt);

                MatchEvaluationFeedbackDTO feedback = chatClient.prompt()
                        .system("Du bist ein Qualitätsprüfer für den SkillSwap Matching-Algorithmus. " +
                                "Prüfe nur, ob der Score logisch zum Thema von Angebot und Gesuch passt. " +
                                "Akzeptiere das Ergebnis (isAccepted=true), auch wenn der Score niedrig ist, solange die Bewertung inhaltlich korrekt ist (z.B. schlechtes Match = niedriger Score). " +
                                "Lehne es nur ab (isAccepted=false), wenn der Score völlig unlogisch ist (z.B. hohes Match für völlig unterschiedliche Themen). " +
                                "WICHTIG: Antworte STRIKT im JSON Format mit exakt zwei Feldern: 'isAccepted' (als boolean) und 'improvements' (als Text).")
                        .user(evalPrompt)
                        .advisors(feedbackAdvisor)
                        .call()
                        .entity(MatchEvaluationFeedbackDTO.class);

                if (feedback != null && feedback.isAccepted()) {
                    System.out.println("EVALUATOR: Akzeptiert!");
                    return Math.min(100, Math.max(0, result.score()));
                } else {
                    previousFeedback = feedback != null ? feedback.improvements() : "Unbekannter Fehler.";
                    System.out.println("EVALUATOR: Abgelehnt. Verbesserungsvorschlag: " + previousFeedback);
                }
            } catch (Exception e) {
                System.err.println("Fehler beim KI-Matching (Iteration " + i + "): " + e.getMessage());
                // Wir brechen bei harten Fehlern (z.B. Timeout) lieber sofort ab, anstatt es 3x
                // zu versuchen
                return 0;
            }
        }

        System.err.println("Fehler: Evaluator hat das Match 3x abgelehnt. Fallback Score 0.");
        return 0;
    }
}