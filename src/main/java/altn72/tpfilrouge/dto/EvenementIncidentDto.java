package altn72.tpfilrouge.dto;

import altn72.tpfilrouge.modele.TypeEvenement;

import java.time.Instant;

public record EvenementIncidentDto(Long id, TypeEvenement type, String details, String auteur, Instant dateEvenement) {
}
