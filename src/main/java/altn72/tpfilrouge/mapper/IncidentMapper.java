package altn72.tpfilrouge.mapper;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.ModifierIncidentRequestDto;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.Station;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class IncidentMapper {
    public Incident declarerIncidentRequestToEntity(DeclarerIncidentRequestDto requete, Equipement equipement, Instant dateOuverture, Station station) {
        Incident incident = new Incident();
        incident.setDateOuverture(dateOuverture);
        incident.setEquipement(equipement);
        incident.setDescription(requete.description());
        incident.setStation(station);
        incident.setStatut("NON_AFFECTE");

        return incident;
    }

    public IncidentDto toIncidentDto(Incident incident) {
        return new IncidentDto(
                incident.getId(),
                incident.getDescription(),
                incident.getDateOuverture(),
                incident.getStatut(),
                incident.getDateResolution()
        );
    }

    public Incident modifierIncidentRequesttoEntity(ModifierIncidentRequestDto requete,
                                                    Equipement equipement,
                                                    Station station,
                                                    Prestataire prestataire) {
        Incident incident = new Incident();
        incident.setDescription(requete.description());
        incident.setDateOuverture(requete.dateOuverture());
        incident.setStatut(requete.statut());
        incident.setDateResolution(requete.dateResolution());
        incident.setEquipement(equipement);
        incident.setStation(station);
        incident.setPrestataire(prestataire);
        return incident;
    }
}
