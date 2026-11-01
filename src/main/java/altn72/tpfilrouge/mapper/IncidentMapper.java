package altn72.tpfilrouge.mapper;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.ModifierIncidentRequestDto;
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

    public IncidentDto toIncidentDto(Incident incident, Duration dateOuverture, NiveauAttention niveauAttention) {
        return new IncidentDto(
                incident.getId(),
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getStatut(),
                incident.getDateResolution(),
                incident.getEquipement(),
                incident.getPrestataire(),
                incident.getEvaluation(),
                dateOuverture,
                niveauAttention
        );
    }

    public Incident modifierIncidentRequesttoEntity(ModifierIncidentRequestDto requete,
                                                    Equipement equipement,
                                                    Prestataire prestataire) {
        Incident incident = new Incident();
        incident.setDescription(requete.description());
        incident.setDateOuverture(requete.dateOuverture());
        incident.setStatut(requete.statut());
        incident.setDateResolution(requete.dateResolution());
        incident.setEquipement(equipement);
        incident.setPrestataire(prestataire);
        return incident;
    }
}
