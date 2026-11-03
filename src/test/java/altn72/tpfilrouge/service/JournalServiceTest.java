package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.EvenementIncidentDto;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.modele.EvenementIncident;
import altn72.tpfilrouge.modele.TypeEvenement;
import altn72.tpfilrouge.repository.EvenementIncidentRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalServiceTest {

    @Mock
    private EvenementIncidentRepository evenementIncidentRepository;
    @Mock
    private IncidentRepository incidentRepository;

    @InjectMocks
    private JournalService journalService;

    @Test
    void leJournalRestitueLesEvenementsDansLOrdreDuRepository() {
        when(incidentRepository.existsById(1L)).thenReturn(true);
        when(evenementIncidentRepository.findByIncidentIdOrderByDateEvenementAsc(1L))
                .thenReturn(List.of(evenement(TypeEvenement.DECLARATION), evenement(TypeEvenement.AFFECTATION)));

        List<EvenementIncidentDto> journal = journalService.getJournal(1L);

        assertThat(journal).extracting(EvenementIncidentDto::type).containsExactly(TypeEvenement.DECLARATION, TypeEvenement.AFFECTATION);
    }

    @Test
    void consulterLeJournalDUnIncidentInexistantLeveUneException() {
        when(incidentRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> journalService.getJournal(99L))
                .isInstanceOf(IncidentIntrouvableException.class);
    }

    private EvenementIncident evenement(TypeEvenement type) {
        EvenementIncident evenement = new EvenementIncident();
        evenement.setType(type);
        return evenement;
    }
}
