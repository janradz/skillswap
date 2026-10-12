package de.fhmuenster.skillswap.aspect;

import de.fhmuenster.skillswap.scope.RequestTracker;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;

@Component
@Aspect
public class PerformanceAspect
{
    private static final Logger logger = LoggerFactory.getLogger(PerformanceAspect.class);
    private final RequestTracker requestTracker;

    public PerformanceAspect(RequestTracker requestTracker)
    {
        this.requestTracker = requestTracker;
    }

    // Wir können wieder alle Service-Methoden abfangen (ohne das !@annotation)
    @Around("execution(* de.fhmuenster.skillswap.service.*.*(..))")
    public Object measurePerformance(ProceedingJoinPoint joinPoint) throws Throwable
    {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        // Standardwerte für Hintergrund-Tasks
        String reqIdStr = "BACKGROUND-TASK";
        String reqTimeStr = "N/A";

        // prüfen, ob ein echter HTTP-Request aktiv ist
        if (RequestContextHolder.getRequestAttributes() != null) {
            reqIdStr = requestTracker.getRequestId().toString();
            reqTimeStr = requestTracker.getTimestamp().toString();
        }

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;

            if (duration > 100)
            {
                logger.warn("Langsame Methode: {} dauerte {}ms aufgerufen in Request: {} um {}", methodName, duration, reqIdStr, reqTimeStr);
            }
            else
            {
                logger.info("Methode: {} dauerte {}ms aufgerufen in Request: {} um {}", methodName, duration, reqIdStr, reqTimeStr);
            }

            return result;

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            logger.error("Fehler in {} nach {}ms: {} aufgerufen in Request: {} um {}", methodName, duration, e.getMessage(), reqIdStr, reqTimeStr);
            throw e;
        }
    }
}