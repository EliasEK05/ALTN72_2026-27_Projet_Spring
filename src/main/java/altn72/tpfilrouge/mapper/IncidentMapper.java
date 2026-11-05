package altn72.tpfilrouge.mapper;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.modele.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class IncidentMapper {
    public Incident declarerIncidentRequestToEntity(DeclarerIncidentRequestDto requete, Equipement equipement, Instant dateOuverture) {
        Incident incident = new Incident();
        incident.setDateOuverture(dateOuverture);
        incident.setEquipement(equipement);
        incident.setDescription(requete.description());
        incident.setStatut(StatutIncident.NON_AFFECTE);

        return incident;
    }

    public IncidentDto toIncidentDto(Incident incident, Duration dureeOuverture, NiveauAttention niveauAttention, boolean equipementFragile) {
        Equipement equipement = incident.getEquipement();
        Prestataire prestataire = incident.getPrestataire();
        Evaluation evaluation = incident.getEvaluation();

        return new IncidentDto(
                incident.getId(),
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getStatut(),
                incident.getDateResolution(),
                equipement.getId(),
                equipement.getReference(),
                equipement.getLibelle(),
                equipement.getStation().getNom(),
                prestataire != null ? prestataire.getId() : null,
                prestataire != null ? prestataire.getNom() : null,
                evaluation != null ? evaluation.getNote() : null,
                evaluation != null ? evaluation.getCommentaire() : null,
                dureeOuverture,
                niveauAttention,
                equipementFragile
        );
    }
}
