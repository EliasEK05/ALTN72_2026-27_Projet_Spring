package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.IndicateursDto;
import altn72.tpfilrouge.dto.TableauDeBordDto;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.IncidentsActifsParStation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TableauDeBordServiceTest {

    @Mock
    private IncidentService incidentService;
    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private TableauDeBordService tableauDeBordService;

    private final List<IncidentDto> incidentsActifs = List.of(
            incident(1L, NiveauAttention.ROUGE),
            incident(2L, NiveauAttention.ROUGE),
            incident(3L, NiveauAttention.ORANGE),
            incident(4L, NiveauAttention.VERT),
            incident(5L, NiveauAttention.AFFECTE));

    @Test
    void lesIndicateursComptentLesIncidentsActifsParNiveauDAttention() {
        when(incidentService.getIncidentsActifs(null, null)).thenReturn(incidentsActifs);
        when(incidentRepository.compterIncidentsActifsParStation()).thenReturn(List.of());

        IndicateursDto indicateurs = tableauDeBordService.getTableauDeBord(null, null, null).indicateurs();

        assertThat(indicateurs.nombreIncidentsActifs()).isEqualTo(5);
        assertThat(indicateurs.nombreRouges()).isEqualTo(2);
        assertThat(indicateurs.nombreOranges()).isEqualTo(1);
        assertThat(indicateurs.nombreVerts()).isEqualTo(1);
        assertThat(indicateurs.nombreAffectes()).isEqualTo(1);
    }

    @Test
    void lesIncidentsPeuventEtreFiltresParNiveauDAttention() {
        when(incidentService.getIncidentsActifs(null, null)).thenReturn(incidentsActifs);
        when(incidentRepository.compterIncidentsActifsParStation()).thenReturn(List.of());

        TableauDeBordDto tableauDeBord = tableauDeBordService.getTableauDeBord(null, null, NiveauAttention.ROUGE);

        assertThat(tableauDeBord.incidents()).extracting(IncidentDto::id).containsExactly(1L, 2L);
    }

    @Test
    void lesIncidentsParStationVenantDeLaRequeteNativeSontRepris() {
        when(incidentService.getIncidentsActifs(null, null)).thenReturn(List.of());
        when(incidentRepository.compterIncidentsActifsParStation()).thenReturn(List.of(ligne("Châtelet", 3L)));

        IndicateursDto indicateurs = tableauDeBordService.getTableauDeBord(null, null, null).indicateurs();

        assertThat(indicateurs.incidentsParStation()).hasSize(1);
        assertThat(indicateurs.incidentsParStation().getFirst().stationNom()).isEqualTo("Châtelet");
        assertThat(indicateurs.incidentsParStation().getFirst().nombreIncidents()).isEqualTo(3L);
    }

    private IncidentDto incident(Long id, NiveauAttention niveauAttention) {
        return new IncidentDto(id, "Panne", null, StatutIncident.NON_AFFECTE, null, 1L, "ASC-001",
                "Ascenseur", "Châtelet", null, null, null, null, null, niveauAttention, false);
    }

    private IncidentsActifsParStation ligne(String stationNom, Long nombreIncidents) {
        return new IncidentsActifsParStation() {
            @Override
            public String getStationNom() {
                return stationNom;
            }

            @Override
            public Long getNombreIncidents() {
                return nombreIncidents;
            }
        };
    }
}
