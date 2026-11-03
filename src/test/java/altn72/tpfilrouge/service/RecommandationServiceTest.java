package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.RecommandationPrestataireDto;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.exception.TransitionIncidentInterditeException;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommandationServiceTest {
    private static final String ASCENSEUR = "ASCENSEUR";

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private PrestataireRepository prestataireRepository;
    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private RecommandationService recommandationService;

    @Test
    void lesPrestatairesSontClassesParNoteMoyenneDecroissanteEtLesNonEvaluesEnDernier() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentSurUnAscenseur()));
        when(prestataireRepository.findByCompetence(ASCENSEUR)).thenReturn(List.of(prestataire(1L, "Kone"), prestataire(2L, "Otis"), prestataire(3L, "Schindler")));
        when(evaluationRepository.calculerNoteMoyenneParType(1L, ASCENSEUR)).thenReturn(3.0);
        when(evaluationRepository.calculerNoteMoyenneParType(2L, ASCENSEUR)).thenReturn(4.5);
        when(evaluationRepository.calculerNoteMoyenneParType(3L, ASCENSEUR)).thenReturn(null);

        List<RecommandationPrestataireDto> recommandations = recommandationService.recommanderPrestataires(1L);

        assertThat(recommandations).extracting(RecommandationPrestataireDto::nom).containsExactly("Otis", "Kone", "Schindler");
    }

    @Test
    void unPrestataireTropChargeNEstPasRecommande() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentSurUnAscenseur()));
        when(prestataireRepository.findByCompetence(ASCENSEUR)).thenReturn(List.of(prestataire(1L, "Kone"), prestataire(2L, "Otis")));
        when(evaluationRepository.calculerNoteMoyenneParType(1L, ASCENSEUR)).thenReturn(5.0);
        when(evaluationRepository.calculerNoteMoyenneParType(2L, ASCENSEUR)).thenReturn(3.0);
        when(incidentRepository.countByPrestataireIdAndStatut(1L, StatutIncident.AFFECTE)).thenReturn(3L);

        List<RecommandationPrestataireDto> recommandations = recommandationService.recommanderPrestataires(1L);

        assertThat(recommandations).extracting(RecommandationPrestataireDto::nom).containsExactly("Otis");
    }

    @Test
    void auPlusTroisPrestatairesSontRecommandes() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentSurUnAscenseur()));
        when(prestataireRepository.findByCompetence(ASCENSEUR)).thenReturn(List.of(prestataire(1L, "Kone"), prestataire(2L, "Otis"), prestataire(3L, "Schindler"), prestataire(4L, "Thyssen")));

        List<RecommandationPrestataireDto> recommandations = recommandationService.recommanderPrestataires(1L);

        assertThat(recommandations).hasSize(3);
    }

    @Test
    void chaqueRecommandationEstArgumentee() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentSurUnAscenseur()));
        when(prestataireRepository.findByCompetence(ASCENSEUR)).thenReturn(List.of(prestataire(1L, "Kone")));
        when(evaluationRepository.calculerNoteMoyenneParType(1L, ASCENSEUR)).thenReturn(null);
        when(incidentRepository.countByPrestataireIdAndEquipementTypeAndStatut(1L, ASCENSEUR, StatutIncident.RESOLU)).thenReturn(2L);
        when(incidentRepository.countByPrestataireIdAndStatut(1L, StatutIncident.AFFECTE)).thenReturn(1L);

        RecommandationPrestataireDto recommandation = recommandationService.recommanderPrestataires(1L).getFirst();

        assertThat(recommandation.arguments()).containsExactly(
                "Compétent sur les équipements de type ASCENSEUR",
                "Pas encore évalué sur ce type d'équipement",
                "2 intervention(s) résolue(s) sur ce type d'équipement",
                "1 incident(s) en cours");
    }

    @Test
    void unIncidentResoluNePeutPasRecevoirDeRecommandation() {
        Incident incident = incidentSurUnAscenseur();
        incident.setStatut(StatutIncident.RESOLU);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThatThrownBy(() -> recommandationService.recommanderPrestataires(1L))
                .isInstanceOf(TransitionIncidentInterditeException.class)
                .hasMessageContaining("est résolu");
        verify(prestataireRepository, never()).findByCompetence(anyString());
    }

    @Test
    void recommanderPourUnIncidentInexistantLeveUneException() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> recommandationService.recommanderPrestataires(99L))
                .isInstanceOf(IncidentIntrouvableException.class);
    }

    private Incident incidentSurUnAscenseur() {
        Equipement equipement = new Equipement();
        equipement.setType(ASCENSEUR);

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatut(StatutIncident.NON_AFFECTE);
        incident.setEquipement(equipement);
        return incident;
    }

    private Prestataire prestataire(Long id, String nom) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        prestataire.setNom(nom);
        return prestataire;
    }
}
