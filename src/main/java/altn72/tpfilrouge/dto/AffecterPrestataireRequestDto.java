package altn72.tpfilrouge.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record AffecterPrestataireRequestDto(
        @NotNull(message = "Le prestataire est obligatoire")
        Long prestataireId
    ) implements Serializable {
}
