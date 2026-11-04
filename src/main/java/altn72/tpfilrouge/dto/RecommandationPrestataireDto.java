package altn72.tpfilrouge.dto;

import java.util.List;

public record RecommandationPrestataireDto(Long prestataireId, String nom, String contact, Double noteMoyenne,
                                           long nbInterventions, long nbIncidentsEnCours, List<String> arguments) {
}
