package altn72.tpfilrouge.evenement;

import altn72.tpfilrouge.modele.EvenementIncident;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.TypeEvenement;
import altn72.tpfilrouge.repository.EvenementIncidentRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.service.UtilisateurCourant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JournalListenerTest {
    private static final Instant MAINTENANT = Instant.parse("2026-11-01T12:00:00Z");

    @Mock
    private EvenementIncidentRepository evenementIncidentRepository;
    @Mock
    private IncidentRepository incidentRepository;

    private JournalListener journalListener;

    @BeforeEach
    void initialiser() {
        journalListener = new JournalListener(evenementIncidentRepository, incidentRepository, new UtilisateurCourant(), Clock.fixed(MAINTENANT, ZoneOffset.UTC));
    }

    @Test
    void unEvenementEstEnregistreAvecSonAuteurEtLaDateCourante() {
        Incident incident = new Incident();
        when(incidentRepository.getReferenceById(1L)).thenReturn(incident);
        ArgumentCaptor<EvenementIncident> captor = ArgumentCaptor.forClass(EvenementIncident.class);

        journalListener.journaliser(new IncidentModifieEvenement(1L, TypeEvenement.RESOLUTION, "Incident résolu"));

        verify(evenementIncidentRepository).save(captor.capture());
        EvenementIncident evenement = captor.getValue();
        assertThat(evenement.getIncident()).isSameAs(incident);
        assertThat(evenement.getType()).isEqualTo(TypeEvenement.RESOLUTION);
        assertThat(evenement.getDetails()).isEqualTo("Incident résolu");
        assertThat(evenement.getAuteur()).isEqualTo("Système");
        assertThat(evenement.getDateEvenement()).isEqualTo(MAINTENANT);
    }
}
