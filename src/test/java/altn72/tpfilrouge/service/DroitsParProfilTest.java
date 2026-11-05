package altn72.tpfilrouge.service;

import altn72.tpfilrouge.mapper.IncidentMapper;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// RG11 Les vrais services sont chargés par Spring pour que les restrictions par profil soient appliquées
@SpringJUnitConfig
class DroitsParProfilTest {

    @Configuration
    @EnableMethodSecurity
    @Import({IncidentService.class, EvaluationService.class, RecommandationService.class, TableauDeBordService.class})
    static class ConfigurationDuTest {
    }

    @MockitoBean
    private IncidentRepository incidentRepository;
    @MockitoBean
    private EquipementRepository equipementRepository;
    @MockitoBean
    private PrestataireRepository prestataireRepository;
    @MockitoBean
    private EvaluationRepository evaluationRepository;
    @MockitoBean
    private IncidentMapper incidentMapper;
    @MockitoBean
    private CalculateurNiveauAttention calculateurNiveauAttention;
    @MockitoBean
    private DetecteurEquipementFragile detecteurEquipementFragile;
    @MockitoBean
    private Clock clock;

    @Autowired
    private IncidentService incidentService;
    @Autowired
    private EvaluationService evaluationService;
    @Autowired
    private RecommandationService recommandationService;
    @Autowired
    private TableauDeBordService tableauDeBordService;

    @Test
    @WithMockUser(roles = "AGENT_TERRAIN")
    void lAgentNePeutPasAffecterUnPrestataire() {
        assertThatThrownBy(() -> incidentService.affecterPrestataire(1L, 1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "AGENT_TERRAIN")
    void lAgentNePeutPasDeclarerUnIncidentResolu() {
        assertThatThrownBy(() -> incidentService.resoudre(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "AGENT_TERRAIN")
    void lAgentNePeutPasEvaluerUnPrestataire() {
        assertThatThrownBy(() -> evaluationService.evaluer(1L, 5, "Parfait"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "AGENT_TERRAIN")
    void lAgentNePeutPasDemanderUneRecommandationDePrestataire() {
        assertThatThrownBy(() -> recommandationService.recommanderPrestataires(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "AGENT_TERRAIN")
    void lAgentNePeutPasConsulterLeTableauDeBord() {
        assertThatThrownBy(() -> tableauDeBordService.getTableauDeBord(null, null, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "RESPONSABLE_EXPLOITATION")
    void leResponsablePeutConsulterLeTableauDeBord() {
        assertThatNoException().isThrownBy(() -> tableauDeBordService.getTableauDeBord(null, null, null));
    }
}
