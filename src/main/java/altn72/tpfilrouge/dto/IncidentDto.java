package altn72.tpfilrouge.dto;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Evaluation;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.StatutIncident;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

/**
 * DTO for {@link altn72.tpfilrouge.modele.Incident}
 */
public record IncidentDto(
        Long id,
        @NotBlank(message = "La description est obligatoire")
        String description,
        @NotNull(message = "La date d'ouverture est obligatoire")
        Instant dateOuverture,
        @NotNull(message = "Le statut est obligatoire")
        StatutIncident statut,
        Instant dateResolution,
        @NotNull(message = "L'équipement est obligatoire")
        Equipement equipement,
        @NotNull(message = "Le prestataire est obligatoire")
        Prestataire prestataire,
        Evaluation evaluation,
        @NotNull(message = "La durée d'ouverture est obligatoire")
        Duration dureeOuverture,
        @NotNull(message = "Le niveau d'attention est obligatoire")
        NiveauAttention niveauAttention
    ) implements Serializable {
}