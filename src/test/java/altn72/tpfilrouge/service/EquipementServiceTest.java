package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.EquipementDto;
import altn72.tpfilrouge.dto.FicheEquipement;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.exception.EquipementIntrouvableException;
import altn72.tpfilrouge.exception.StationIntrouvableException;
import altn72.tpfilrouge.mapper.EquipementMapper;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.repository.StationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EquipementServiceTest {

    @Mock
    private EquipementRepository equipementRepository;
    @Mock
    private StationRepository stationRepository;
    @Mock
    private IncidentService incidentService;
    @Mock
    private DetecteurEquipementFragile detecteurEquipementFragile;

    private EquipementService equipementService;

    @BeforeEach
    void initialiser() {
        equipementService = new EquipementService(equipementRepository, stationRepository, incidentService, new EquipementMapper(), detecteurEquipementFragile);
    }

    @Test
    void lesEquipementsDUneStationSontRenvoyesAvecLeurTypeEtLeurEtat() {
        when(stationRepository.existsById(1L)).thenReturn(true);
        when(equipementRepository.findByStationId(1L)).thenReturn(List.of(equipement(10L)));

        List<EquipementDto> equipements = equipementService.getEquipementsDeLaStation(1L);

        assertThat(equipements).hasSize(1);
        assertThat(equipements.getFirst().type()).isEqualTo("Ascenseur");
        assertThat(equipements.getFirst().etatFonctionnement()).isEqualTo("En panne");
    }

    @Test
    void consulterLesEquipementsDUneStationInexistanteLeveUneException() {
        when(stationRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> equipementService.getEquipementsDeLaStation(99L))
                .isInstanceOf(StationIntrouvableException.class);
    }

    @Test
    void laFicheDUnEquipementContientSaStationEtSesIncidents() {
        IncidentDto incident = mock(IncidentDto.class);
        when(equipementRepository.findById(10L)).thenReturn(Optional.of(equipement(10L)));
        when(incidentService.getIncidentsByEquipement(10L)).thenReturn(List.of(incident));

        FicheEquipement fiche = equipementService.getFicheEquipement(10L);

        assertThat(fiche.reference()).isEqualTo("ASC-001");
        assertThat(fiche.stationNom()).isEqualTo("Châtelet");
        assertThat(fiche.incidents()).containsExactly(incident);
    }

    @Test
    void laFicheIndiqueSiLEquipementEstFragile() {
        when(equipementRepository.findById(10L)).thenReturn(Optional.of(equipement(10L)));
        when(detecteurEquipementFragile.estFragile(10L)).thenReturn(true);

        assertThat(equipementService.getFicheEquipement(10L).fragile()).isTrue();
    }

    @Test
    void consulterLaFicheDUnEquipementInexistantLeveUneException() {
        when(equipementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> equipementService.getFicheEquipement(99L))
                .isInstanceOf(EquipementIntrouvableException.class);
    }

    private Equipement equipement(Long id) {
        Station station = new Station();
        station.setNom("Châtelet");

        Equipement equipement = new Equipement();
        equipement.setId(id);
        equipement.setReference("ASC-001");
        equipement.setLibelle("Ascenseur quai 1");
        equipement.setType("Ascenseur");
        equipement.setEtatFonctionnement("En panne");
        equipement.setStation(station);
        return equipement;
    }
}
