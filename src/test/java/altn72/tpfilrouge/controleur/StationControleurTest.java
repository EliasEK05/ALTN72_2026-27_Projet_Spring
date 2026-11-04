package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.config.SecuriteConfig;
import altn72.tpfilrouge.exception.StationIntrouvableException;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StationControleur.class)
@Import(SecuriteConfig.class)
@WithMockUser
class StationControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StationService stationService;

    @MockitoBean
    private EquipementService equipementService;

    @Test
    void demanderLesEquipementsDUneStationInexistanteRenvoie404() throws Exception {
        when(equipementService.getEquipementsDeLaStation(99L)).thenThrow(new StationIntrouvableException(99L));

        mockMvc.perform(get("/station/99/equipements"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("La station n°99 est introuvable"));
    }
}
