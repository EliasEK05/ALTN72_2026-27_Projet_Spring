package altn72.tpfilrouge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Instant;

/**
 * DTO for {@link altn72.tpfilrouge.modele.Incident}
 */
public record IncidentDto(
        Long id,
        @NotBlank(message = "La description est obligatoire")
        String description,
        @NotNull(message = "La date d'ouverture est obligatoire")
        Instant dateOuverture,
        @NotBlank(message = "Le statut est obligatoire")
        String statut,
        Instant dateResolution)
        implements Serializable {
}