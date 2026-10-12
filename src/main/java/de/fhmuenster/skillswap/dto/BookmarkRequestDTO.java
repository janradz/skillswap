package de.fhmuenster.skillswap.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

/**
 * Könnte man im BookmarkController verwenden, wäre aber aktuell ein bisschen drüber, weil @Pathvariable ausreicht?
 */
public class BookmarkRequestDTO
{
    @NotNull(message = "Offer ID darf nicht leer sein")
    private UUID offerId;

    public UUID getOfferId() { return offerId; }
    public void setOfferId(UUID offerId) { this.offerId = offerId; }
}
