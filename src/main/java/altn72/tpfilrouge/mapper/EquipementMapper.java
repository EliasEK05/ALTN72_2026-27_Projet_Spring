package altn72.tpfilrouge.mapper;

import altn72.tpfilrouge.dto.EquipementDto;
import altn72.tpfilrouge.dto.FicheEquipement;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.modele.Equipement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EquipementMapper {
    public EquipementDto toEquipementDto(Equipement equipement) {
        return new EquipementDto(
                equipement.getId(),
                equipement.getReference(),
                equipement.getLibelle(),
                equipement.getType(),
                equipement.getEtatFonctionnement());
    }

    public FicheEquipement toFicheEquipement(Equipement equipement, boolean fragile, List<IncidentDto> incidents) {
        return new FicheEquipement(
                equipement.getId(),
                equipement.getReference(),
                equipement.getLibelle(),
                equipement.getType(),
                equipement.getEtatFonctionnement(),
                equipement.getStation().getNom(),
                fragile,
                incidents);
    }
}
