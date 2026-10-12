package de.fhmuenster.skillswap.aspect;

import de.fhmuenster.skillswap.model.Enquiry;
import de.fhmuenster.skillswap.model.Offer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class MatchLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(MatchLoggingAspect.class);

    // Loggt den Start der Matching-Routine
    @Before("execution(* de.fhmuenster.skillswap.service.MatchEngineService.runMatchingRoutine(..))")
    public void logMatchingStart() {
        logger.info("Matching-Routine gestartet...");
    }

    // Loggt das Ende der Matching-Routine
    @After("execution(* de.fhmuenster.skillswap.service.MatchEngineService.runMatchingRoutine(..))")
    public void logMatchingEnd() {
        logger.info("Matching-Routine beendet.");
    }

    // Loggt die Details, wenn ein Match erstellt wird
    @Before("execution(* de.fhmuenster.skillswap.service.MatchService.createAutomaticMatch(..))")
    public void logMatchFound(JoinPoint joinPoint) {
        // Wir holen uns die Parameter, die der Methode übergeben wurden
        Object[] args = joinPoint.getArgs();
        Offer offer = (Offer) args[0];
        Enquiry enquiry = (Enquiry) args[1];
        int score = (int) args[2];

        logger.info("Match gefunden zwischen Offer {} und Enquiry {}! Score: {}",
                offer.getId(), enquiry.getId(), score);
    }
}