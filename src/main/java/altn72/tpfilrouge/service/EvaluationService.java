package altn72.tpfilrouge.service;

import altn72.tpfilrouge.config.ReserveAuResponsable;
import altn72.tpfilrouge.dto.EvaluationDto;
import altn72.tpfilrouge.evenement.IncidentModifieEvenement;
import altn72.tpfilrouge.exception.EvaluationImpossibleException;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.modele.Evaluation;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.modele.TypeEvenement;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
public class EvaluationService {
    private final EvaluationRepository evaluationRepository;
    private final IncidentRepository incidentRepository;
    private final Clock clock;
    private final ApplicationEventPublisher evenementPublisher;

    public EvaluationService(EvaluationRepository evaluationRepository, IncidentRepository incidentRepository, Clock clock, ApplicationEventPublisher evenementPublisher) {
        this.evaluationRepository = evaluationRepository;
        this.incidentRepository = incidentRepository;
        this.clock = clock;
        this.evenementPublisher = evenementPublisher;
    }

    // F10 Évaluer l'intervention du prestataire d'un incident résolu (RG8)
    @ReserveAuResponsable
    @Transactional
    public EvaluationDto evaluer(Long incidentId, Integer note, String commentaire) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentIntrouvableException(incidentId));
        verifierEvaluable(incident);

        Evaluation evaluation = new Evaluation();
        evaluation.setIncident(incident);
        evaluation.setNote(note);
        evaluation.setCommentaire(commentaire);
        evaluation.setDateEvaluation(Instant.now(clock));
        incident.setEvaluation(evaluation);

        EvaluationDto evaluationDto = versDto(evaluationRepository.save(evaluation));
        evenementPublisher.publishEvent(new IncidentModifieEvenement(incidentId, TypeEvenement.EVALUATION,
                incident.getPrestataire().getNom() + " évalué " + note + "/5"));
        return evaluationDto;
    }

    private void verifierEvaluable(Incident incident) {
        if (incident.getStatut() != StatutIncident.RESOLU) {
            throw new EvaluationImpossibleException("L'incident n°" + incident.getId() + " doit être résolu pour être évalué");
        }
        if (incident.getPrestataire() == null) {
            throw new EvaluationImpossibleException("L'incident n°" + incident.getId() + " n'a été affecté à aucun prestataire");
        }
        if (evaluationRepository.existsByIncidentId(incident.getId())) {
            throw new EvaluationImpossibleException("L'incident n°" + incident.getId() + " a déjà été évalué");
        }
    }

    private EvaluationDto versDto(Evaluation evaluation) {
        Incident incident = evaluation.getIncident();
        return new EvaluationDto(
                evaluation.getId(),
                incident.getId(),
                incident.getPrestataire().getNom(),
                evaluation.getNote(),
                evaluation.getCommentaire(),
                evaluation.getDateEvaluation());
    }
}
