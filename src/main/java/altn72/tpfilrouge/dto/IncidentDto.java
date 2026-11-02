package altn72.tpfilrouge.dto;

import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.StatutIncident;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

/**
 * DTO for {@link altn72.tpfilrouge.modele.Incident}
 */
public record IncidentDto(
        Long id,
        String description,
        Instant dateOuverture,
        StatutIncident statut,
        Instant dateResolution,
        Long equipementId,
        String equipementReference,
        String equipementLibelle,
        String stationNom,
        Long prestataireId,
        String prestataireNom,
        Integer note,
        String commentaireEvaluation,
        Duration dureeOuverture,
        NiveauAttention niveauAttention
    ) implements Serializable {
}
