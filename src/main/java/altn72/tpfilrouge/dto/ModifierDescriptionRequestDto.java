package altn72.tpfilrouge.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;

public record ModifierDescriptionRequestDto(
        @NotBlank(message = "La description est obligatoire")
        String description
    ) implements Serializable {
}
