package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.config.SecuriteConfig;
import altn72.tpfilrouge.exception.EvaluationImpossibleException;
import altn72.tpfilrouge.service.EvaluationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EvaluationControleur.class)
@Import(SecuriteConfig.class)
@WithMockUser
class EvaluationControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EvaluationService evaluationService;

    @Test
    void uneNoteSuperieureA5Renvoie400() throws Exception {
        mockMvc.perform(post("/incident/1/evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": 6}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La note doit être comprise entre 1 et 5"));
    }

    @Test
    void uneNoteInferieureA1Renvoie400() throws Exception {
        mockMvc.perform(post("/incident/1/evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": 0}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void evaluerUnIncidentDejaEvalueRenvoie409() throws Exception {
        when(evaluationService.evaluer(1L, 4, null))
                .thenThrow(new EvaluationImpossibleException("L'incident n°1 a déjà été évalué"));

        mockMvc.perform(post("/incident/1/evaluation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\": 4}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("L'incident n°1 a déjà été évalué"));
    }
}
