package altn72.tpfilrouge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link altn72.tpfilrouge.modele.Incident}
 */
public record DeclarerIncidentRequestDto(
        @NotNull(message = "L'équipement est obligatoire")
        Long equipementId,

        @NotBlank(message = "La description est obligatoire")
        String description) implements Serializable {
}