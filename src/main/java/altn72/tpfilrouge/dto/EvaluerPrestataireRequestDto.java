package altn72.tpfilrouge.dto;

import altn72.tpfilrouge.modele.Evaluation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

public record EvaluerPrestataireRequestDto(
        @NotNull(message = "La note est obligatoire")
        @Min(value = Evaluation.NOTE_MINIMALE, message = "La note doit être comprise entre 1 et 5")
        @Max(value = Evaluation.NOTE_MAXIMALE, message = "La note doit être comprise entre 1 et 5")
        Integer note,

        String commentaire
    ) implements Serializable {
}
