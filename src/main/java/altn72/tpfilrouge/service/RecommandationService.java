package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.RecommandationPrestataireDto;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.exception.TransitionIncidentInterditeException;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class RecommandationService {
    private static final int NB_RECOMMANDATIONS = 3;
    private static final int SEUIL_DISPONIBILITE = 3;

    private final IncidentRepository incidentRepository;
    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;

    public RecommandationService(IncidentRepository incidentRepository, PrestataireRepository prestataireRepository, EvaluationRepository evaluationRepository) {
        this.incidentRepository = incidentRepository;
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
    }

    // Aide à l'affectation : les prestataires disponibles les mieux notés pour le type d'équipement de l'incident
    @Transactional(readOnly = true)
    public List<RecommandationPrestataireDto> recommanderPrestataires(Long incidentId) {
        Incident incident = incidentRepository.findById(incidentId)
                .orElseThrow(() -> new IncidentIntrouvableException(incidentId));
        if (incident.getStatut() == StatutIncident.RESOLU) {
            throw new TransitionIncidentInterditeException("L'incident n°" + incidentId + " est résolu et ne peut plus être affecté");
        }

        String typeEquipement = incident.getEquipement().getType();
        return prestataireRepository.findByCompetence(typeEquipement)
                .stream()
                .map(prestataire -> versDto(prestataire, typeEquipement))
                .filter(recommandation -> recommandation.nbIncidentsEnCours() < SEUIL_DISPONIBILITE)
                .sorted(Comparator.comparing(RecommandationPrestataireDto::noteMoyenne, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(NB_RECOMMANDATIONS)
                .toList();
    }

    private RecommandationPrestataireDto versDto(Prestataire prestataire, String typeEquipement) {
        Double noteMoyenne = evaluationRepository.calculerNoteMoyenneParType(prestataire.getId(), typeEquipement);
        long nbInterventions = incidentRepository.countByPrestataireIdAndEquipementTypeAndStatut(prestataire.getId(), typeEquipement, StatutIncident.RESOLU);
        long nbIncidentsEnCours = incidentRepository.countByPrestataireIdAndStatut(prestataire.getId(), StatutIncident.AFFECTE);

        List<String> arguments = List.of(
                "Compétent sur les équipements de type " + typeEquipement,
                noteMoyenne != null ? "Note moyenne de " + String.format("%.1f", noteMoyenne) + "/5 sur ce type d'équipement" : "Pas encore évalué sur ce type d'équipement",
                nbInterventions + " intervention(s) résolue(s) sur ce type d'équipement",
                nbIncidentsEnCours + " incident(s) en cours");

        return new RecommandationPrestataireDto(
                prestataire.getId(),
                prestataire.getNom(),
                prestataire.getContact(),
                noteMoyenne,
                nbInterventions,
                nbIncidentsEnCours,
                arguments);
    }
}
