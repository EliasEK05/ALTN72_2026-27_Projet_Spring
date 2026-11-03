package altn72.tpfilrouge.dto;

import java.time.Instant;

public record EvaluationDto(Long id, Long incidentId, String prestataireNom, Integer note,
                            String commentaire, Instant dateEvaluation) {
}
