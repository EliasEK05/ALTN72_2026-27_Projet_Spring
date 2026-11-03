package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.EvaluationDto;
import altn72.tpfilrouge.exception.EvaluationImpossibleException;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.modele.Evaluation;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluationServiceTest {
    private static final Instant MAINTENANT = Instant.parse("2026-11-01T12:00:00Z");

    @Mock
    private EvaluationRepository evaluationRepository;
    @Mock
    private IncidentRepository incidentRepository;

    private EvaluationService evaluationService;

    @BeforeEach
    void initialiser() {
        evaluationService = new EvaluationService(evaluationRepository, incidentRepository, Clock.fixed(MAINTENANT, ZoneOffset.UTC));
    }

    @Test
    void unIncidentResoluEtAffectePeutEtreEvalue() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentResoluEtAffecte()));
        when(evaluationRepository.existsByIncidentId(1L)).thenReturn(false);
        when(evaluationRepository.save(any(Evaluation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EvaluationDto evaluation = evaluationService.evaluer(1L, 4, "Intervention rapide");

        assertThat(evaluation.note()).isEqualTo(4);
        assertThat(evaluation.commentaire()).isEqualTo("Intervention rapide");
        assertThat(evaluation.prestataireNom()).isEqualTo("Otis");
        assertThat(evaluation.dateEvaluation()).isEqualTo(MAINTENANT);
    }

    @Test
    void unIncidentNonResoluNePeutPasEtreEvalue() {
        Incident incident = incidentResoluEtAffecte();
        incident.setStatut(StatutIncident.AFFECTE);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThatThrownBy(() -> evaluationService.evaluer(1L, 4, null))
                .isInstanceOf(EvaluationImpossibleException.class)
                .hasMessageContaining("doit être résolu");
        verify(evaluationRepository, never()).save(any(Evaluation.class));
    }

    @Test
    void unIncidentResoluSansPrestataireNePeutPasEtreEvalue() {
        Incident incident = incidentResoluEtAffecte();
        incident.setPrestataire(null);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));

        assertThatThrownBy(() -> evaluationService.evaluer(1L, 4, null))
                .isInstanceOf(EvaluationImpossibleException.class)
                .hasMessageContaining("aucun prestataire");
    }

    @Test
    void unIncidentNePeutEtreEvalueQuUneSeuleFois() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentResoluEtAffecte()));
        when(evaluationRepository.existsByIncidentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> evaluationService.evaluer(1L, 4, null))
                .isInstanceOf(EvaluationImpossibleException.class)
                .hasMessageContaining("déjà été évalué");
        verify(evaluationRepository, never()).save(any(Evaluation.class));
    }

    @Test
    void evaluerUnIncidentInexistantLeveUneException() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> evaluationService.evaluer(99L, 4, null))
                .isInstanceOf(IncidentIntrouvableException.class);
    }

    private Incident incidentResoluEtAffecte() {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(3L);
        prestataire.setNom("Otis");

        Incident incident = new Incident();
        incident.setId(1L);
        incident.setStatut(StatutIncident.RESOLU);
        incident.setPrestataire(prestataire);
        return incident;
    }
}
