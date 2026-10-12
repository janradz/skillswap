package de.fhmuenster.skillswap.scope;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Gehört die Klasse ins Model?
 */
@Component
@RequestScope
public class RequestTracker
{
    private final UUID requestId = UUID.randomUUID();
    private final LocalDateTime timestamp = LocalDateTime.now();

    public UUID getRequestId()
    {
        return requestId;
    }

    public LocalDateTime getTimestamp()
    {
        return timestamp;
    }
}