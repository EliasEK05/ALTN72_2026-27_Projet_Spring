package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.config.SecuriteConfig;
import altn72.tpfilrouge.dto.IndicateursDto;
import altn72.tpfilrouge.dto.TableauDeBordDto;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.service.TableauDeBordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TableauDeBordControleur.class)
@Import(SecuriteConfig.class)
@WithMockUser
class TableauDeBordControleurTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TableauDeBordService tableauDeBordService;

    @Test
    void lesFiltresSontTransmisAuService() throws Exception {
        IndicateursDto indicateurs = new IndicateursDto(0, 0, 0, 0, 0, List.of());
        when(tableauDeBordService.getTableauDeBord(1L, "Ascenseur", NiveauAttention.ROUGE))
                .thenReturn(new TableauDeBordDto(List.of(), indicateurs));

        mockMvc.perform(get("/tableau-de-bord")
                        .param("stationId", "1")
                        .param("typeEquipement", "Ascenseur")
                        .param("niveauAttention", "ROUGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.indicateurs.nombreIncidentsActifs").value(0));
    }

    @Test
    void unNiveauDAttentionInconnuRenvoie400() throws Exception {
        mockMvc.perform(get("/tableau-de-bord").param("niveauAttention", "VIOLET"))
                .andExpect(status().isBadRequest());
    }
}
