package de.fhmuenster.skillswap.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ExceptionHandlingAOP{

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingAOP.class);

    @Around("execution(* de.fhmuenster.skillswap.exception.GlobalExceptionHandler.*(..))")
    public Object logExceptionHandling(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        Exception exception = null;
        for (Object arg : args) {
            if (arg instanceof Exception) {
                exception = (Exception) arg;
                break;
            }
        }

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long duration = System.currentTimeMillis() - startTime;

            if (exception != null) {
                log.info("Exception Handler {} hat {} in {}ms behandelt",
                        methodName, exception.getClass().getSimpleName(), duration);
            }

            return result;

        } catch (Throwable t) {
            log.error("Fehler im Exception Handler: {}", t.getMessage());
            throw t;
        }
    }
}
