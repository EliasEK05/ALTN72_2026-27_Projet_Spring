package altn72.tpfilrouge.modele;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "equipement")
public class Equipement {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "reference", nullable = false, length = 50)
    private String reference;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "type", nullable = false, length = 50)
    private String type;

    @Column(name = "etat_fonctionnement", nullable = false, length = 50)
    private String etatFonctionnement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtatFonctionnement() {
        return etatFonctionnement;
    }

    public void setEtatFonctionnement(String etatFonctionnement) {
        this.etatFonctionnement = etatFonctionnement;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

}