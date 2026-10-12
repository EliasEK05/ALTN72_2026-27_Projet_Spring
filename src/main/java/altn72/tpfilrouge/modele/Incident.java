package altn72.tpfilrouge.modele;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "incident")
public class Incident {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "date_ouverture", nullable = false)
    private Instant dateOuverture;

    @Column(name = "statut", nullable = false, length = 50)
    private String statut;

    @Column(name = "date_resolution")
    private Instant dateResolution;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "equipement_id", nullable = false)
    private Equipement equipement;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(name = "prestataire_id")
    private Prestataire prestataire;

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

}