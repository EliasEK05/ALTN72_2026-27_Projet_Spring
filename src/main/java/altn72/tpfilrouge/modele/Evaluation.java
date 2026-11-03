package altn72.tpfilrouge.modele;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;

@Entity
@Table(name = "evaluation")
public class Evaluation {
    public static final int NOTE_MINIMALE = 1;
    public static final int NOTE_MAXIMALE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Min(NOTE_MINIMALE)
    @Max(NOTE_MAXIMALE)
    @Column(name = "note", nullable = false)
    private Integer note;

    @Lob
    @Column(name = "commentaire")
    private String commentaire;

    @Column(name = "date_evaluation", nullable = false)
    private Instant dateEvaluation;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "incident_id", nullable = false, unique = true)
    private Incident incident;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNote() {
        return note;
    }

    public void setNote(Integer note) {
        this.note = note;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Instant getDateEvaluation() {
        return dateEvaluation;
    }

    public void setDateEvaluation(Instant dateEvaluation) {
        this.dateEvaluation = dateEvaluation;
    }

    public Incident getIncident() {
        return incident;
    }

    public void setIncident(Incident incident) {
        this.incident = incident;
    }

}