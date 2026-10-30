package altn72.tpfilrouge.modele;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @NotBlank(message = "La description est obligatoire")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "La date d'ouverture est obligatoire")
    @Column(name = "date_ouverture", nullable = false)
    private Instant dateOuverture;

    @NotBlank(message = "Le statut est obligatoire")
    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @Column(name = "date_resolution")
    private Instant dateResolution;

    @NotNull(message = "Un équipement est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

    @NotNull(message = "Une station est obligatoire")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "evaluation_id")
    private Evaluation evaluation;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Instant getDateOuverture() {
        return dateOuverture;
    }

    public void setDateOuverture(Instant dateOuverture) {
        this.dateOuverture = dateOuverture;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Instant getDateResolution() {
        return dateResolution;
    }

    public void setDateResolution(Instant dateResolution) {
        this.dateResolution = dateResolution;
    }

    public Equipement getEquipement() {
        return equipement;
    }

    public void setEquipement(Equipement equipement) {
        this.equipement = equipement;
    }

    public Prestataire getPrestataire() {
        return prestataire;
    }

    public void setPrestataire(Prestataire prestataire) {
        this.prestataire = prestataire;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

}