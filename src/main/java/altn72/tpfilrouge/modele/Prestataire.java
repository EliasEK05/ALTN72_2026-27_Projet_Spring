package altn72.tpfilrouge.modele;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "prestataire")
public class Prestataire {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "contact", nullable = false)
    private String contact;

    @ElementCollection
    @CollectionTable(name = "prestataire_competence", joinColumns = @JoinColumn(name = "prestataire_id"))
    @Column(name = "type_equipement", nullable = false, length = 50)
    private Set<String> competences = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Set<String> getCompetences() {
        return competences;
    }

    public void setCompetences(Set<String> competences) {
        this.competences = competences;
    }

}