package altn72.tpfilrouge.mapper;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class IncidentMapper {
    public Incident toIncident(DeclarerIncidentRequestDto incidentDto, Equipement equipement, Instant dateOuverture) {
        Incident incident = new Incident();
        incident.setDateOuverture(dateOuverture);
        incident.setEquipement(equipement);
        incident.setDescription(incidentDto.description());
        incident.setStatut("NON_AFFECTE");

        return incident;
    }

}
