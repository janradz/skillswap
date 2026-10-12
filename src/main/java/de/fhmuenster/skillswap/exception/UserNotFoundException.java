package de.fhmuenster.skillswap.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException
{
    private final UUID id;

    public UserNotFoundException(UUID id)
    {
        super(String.format("User mit der ID: %s konnte nicht gefunden werden", id));
        this.id = id;
    }

    public UUID getId()
    {
        return id;
    }
}
