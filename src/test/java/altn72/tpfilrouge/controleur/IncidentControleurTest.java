package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.config.SecuriteConfig;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.exception.TransitionIncidentInterditeException;
import altn72.tpfilrouge.service.IncidentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IncidentControleur.class)
@Import(SecuriteConfig.class)
@WithMockUser
class IncidentControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IncidentService incidentService;

    @Test
    void demanderUnIncidentInexistantRenvoie404() throws Exception {
        when(incidentService.getIncidentById(99L)).thenThrow(new IncidentIntrouvableException(99L));

        mockMvc.perform(get("/incident/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("L'incident n°99 est introuvable"));
    }

    @Test
    void resoudreUnIncidentDejaResoluRenvoie409() throws Exception {
        when(incidentService.resoudre(1L))
                .thenThrow(new TransitionIncidentInterditeException("L'incident n°1 est résolu et ne peut plus être modifié"));

        mockMvc.perform(patch("/incident/1/resolution"))
                .andExpect(status().isConflict());
    }

    @Test
    void declarerUnIncidentSansDescriptionRenvoie400() throws Exception {
        mockMvc.perform(post("/incident")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"equipementId\": 1, \"description\": \"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("La description est obligatoire"));
    }

    @Test
    void unIdentifiantNonNumeriqueRenvoie400() throws Exception {
        mockMvc.perform(get("/incident/abc"))
                .andExpect(status().isBadRequest());
    }
}
