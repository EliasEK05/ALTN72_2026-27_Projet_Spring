package altn72.tpfilrouge.evenement;

import altn72.tpfilrouge.modele.TypeEvenement;

public record IncidentModifieEvenement(Long incidentId, TypeEvenement type, String details) {
}
