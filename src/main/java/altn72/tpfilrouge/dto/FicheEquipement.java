package altn72.tpfilrouge.dto;

import java.util.List;

public record FicheEquipement(Long id, String reference, String libelle, String type,
                              String etatFonctionnement, List<IncidentResume> incidents) {

}
