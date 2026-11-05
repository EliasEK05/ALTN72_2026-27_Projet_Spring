package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.evenement.IncidentModifieEvenement;
import altn72.tpfilrouge.exception.EquipementIntrouvableException;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.exception.PrestataireIntrouvableException;
import altn72.tpfilrouge.exception.TransitionIncidentInterditeException;
import altn72.tpfilrouge.mapper.IncidentMapper;
import altn72.tpfilrouge.modele.*;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {
    private static final Instant MAINTENANT = Instant.parse("2026-11-01T12:00:00Z");

    @Mock
    private IncidentRepository incidentRepository;
    @Mock
    private EquipementRepository equipementRepository;
    @Mock
    private PrestataireRepository prestataireRepository;
    @Mock
    private ApplicationEventPublisher evenementPublisher;
    @Mock
    private DetecteurEquipementFragile detecteurEquipementFragile;

    private IncidentService incidentService;

    @BeforeEach
    void initialiser() {
        Clock clock = Clock.fixed(MAINTENANT, ZoneOffset.UTC);
        incidentService = new IncidentService(
                incidentRepository,
                equipementRepository,
                prestataireRepository,
                new IncidentMapper(),
                new CalculateurNiveauAttention(clock),
                detecteurEquipementFragile,
                clock,
                evenementPublisher);
    }

    @Test
    void unIncidentDeclareDemarreSansPrestataireAvecLaDateCourante() {
        Equipement equipement = incidentNonAffecte(1L).getEquipement();
        when(equipementRepository.findById(10L)).thenReturn(Optional.of(equipement));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IncidentDto resultat = incidentService.creerIncident(new DeclarerIncidentRequestDto(10L, "Portique bloqué"));

        assertThat(resultat.statut()).isEqualTo(StatutIncident.NON_AFFECTE);
        assertThat(resultat.prestataireId()).isNull();
        assertThat(resultat.dateOuverture()).isEqualTo(MAINTENANT);
        assertThat(resultat.equipementId()).isEqualTo(10L);
        assertThat(resultat.niveauAttention()).isEqualTo(NiveauAttention.VERT);
    }

    @Test
    void unIncidentIndiqueSiSonEquipementEstFragile() {
        Equipement equipement = incidentNonAffecte(1L).getEquipement();
        when(equipementRepository.findById(10L)).thenReturn(Optional.of(equipement));
        when(incidentRepository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(detecteurEquipementFragile.estFragile(10L)).thenReturn(true);

        IncidentDto resultat = incidentService.creerIncident(new DeclarerIncidentRequestDto(10L, "Portique bloqué"));

        assertThat(resultat.equipementFragile()).isTrue();
    }

    @Test
    void declarerUnIncidentSurUnEquipementInexistantLeveUneException() {
        when(equipementRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.creerIncident(new DeclarerIncidentRequestDto(99L, "Portique bloqué")))
                .isInstanceOf(EquipementIntrouvableException.class);
        verify(incidentRepository, never()).save(any(Incident.class));
    }

    @Test
    void consulterUnIncidentInexistantLeveUneException() {
        when(incidentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.getIncidentById(99L))
                .isInstanceOf(IncidentIntrouvableException.class);
    }

    @Test
    void affecterUnPrestatairePasseLIncidentAuStatutAffecte() {
        Incident incident = incidentNonAffecte(1L);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(3L)).thenReturn(Optional.of(prestataire(3L)));

        IncidentDto resultat = incidentService.affecterPrestataire(1L, 3L);

        assertThat(resultat.statut()).isEqualTo(StatutIncident.AFFECTE);
        assertThat(resultat.prestataireId()).isEqualTo(3L);
        assertThat(resultat.niveauAttention()).isEqualTo(NiveauAttention.AFFECTE);
    }

    @Test
    void unIncidentPeutEtreReaffecteAUnAutrePrestataire() {
        Incident incident = incidentNonAffecte(1L);
        incident.setPrestataire(prestataire(3L));
        incident.setStatut(StatutIncident.AFFECTE);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(4L)).thenReturn(Optional.of(prestataire(4L)));

        IncidentDto resultat = incidentService.affecterPrestataire(1L, 4L);

        assertThat(resultat.prestataireId()).isEqualTo(4L);
    }

    @Test
    void affecterUnPrestataireInexistantLeveUneException() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentNonAffecte(1L)));
        when(prestataireRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> incidentService.affecterPrestataire(1L, 99L))
                .isInstanceOf(PrestataireIntrouvableException.class);
    }

    @Test
    void unIncidentResoluNePeutPlusEtreAffecte() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentResolu(1L)));

        assertThatThrownBy(() -> incidentService.affecterPrestataire(1L, 3L))
                .isInstanceOf(TransitionIncidentInterditeException.class);
    }

    @Test
    void resoudreUnIncidentEnregistreLaDateDeResolution() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentNonAffecte(1L)));

        IncidentDto resultat = incidentService.resoudre(1L);

        assertThat(resultat.statut()).isEqualTo(StatutIncident.RESOLU);
        assertThat(resultat.dateResolution()).isEqualTo(MAINTENANT);
    }

    @Test
    void unIncidentDejaResoluNePeutPasEtreResoluUneSecondeFois() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentResolu(1L)));

        assertThatThrownBy(() -> incidentService.resoudre(1L))
                .isInstanceOf(TransitionIncidentInterditeException.class);
    }

    @Test
    void laDescriptionDUnIncidentResoluNePeutPlusEtreModifiee() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentResolu(1L)));

        assertThatThrownBy(() -> incidentService.modifierDescription(1L, "Nouvelle description"))
                .isInstanceOf(TransitionIncidentInterditeException.class);
    }

    @Test
    void lHistoriqueNeContientQueLesIncidentsResolusDansLOrdreDuRepository() {
        Incident plusRecent = incidentResolu(2L);
        Incident plusAncien = incidentResolu(1L);
        when(incidentRepository.findByStatutOrderByDateResolutionDesc(StatutIncident.RESOLU))
                .thenReturn(List.of(plusRecent, plusAncien));

        List<IncidentDto> historique = incidentService.getHistoriqueIncidentsResolus();

        assertThat(historique).extracting(IncidentDto::id).containsExactly(2L, 1L);
        assertThat(historique).allMatch(incident -> incident.statut() == StatutIncident.RESOLU);
    }

    @Test
    void lAffectationDUnPrestataireEstJournalisee() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentNonAffecte(1L)));
        when(prestataireRepository.findById(3L)).thenReturn(Optional.of(prestataire(3L)));

        incidentService.affecterPrestataire(1L, 3L);

        verify(evenementPublisher).publishEvent(new IncidentModifieEvenement(1L, TypeEvenement.AFFECTATION, "Affecté à Prestataire 3"));
    }

    @Test
    void uneReaffectationEstJournaliseeAvecLAncienEtLeNouveauPrestataire() {
        Incident incident = incidentNonAffecte(1L);
        incident.setPrestataire(prestataire(3L));
        incident.setStatut(StatutIncident.AFFECTE);
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incident));
        when(prestataireRepository.findById(4L)).thenReturn(Optional.of(prestataire(4L)));

        incidentService.affecterPrestataire(1L, 4L);

        verify(evenementPublisher).publishEvent(new IncidentModifieEvenement(1L, TypeEvenement.AFFECTATION, "Réaffecté de Prestataire 3 à Prestataire 4"));
    }

    @Test
    void uneActionRefuseeNEstPasJournalisee() {
        when(incidentRepository.findById(1L)).thenReturn(Optional.of(incidentResolu(1L)));

        assertThatThrownBy(() -> incidentService.resoudre(1L))
                .isInstanceOf(TransitionIncidentInterditeException.class);
        verify(evenementPublisher, never()).publishEvent(any(Object.class));
    }

    private Incident incidentNonAffecte(Long id) {
        Station station = new Station();
        station.setNom("Châtelet");

        Equipement equipement = new Equipement();
        equipement.setId(10L);
        equipement.setReference("ASC-001");
        equipement.setLibelle("Ascenseur quai 1");
        equipement.setStation(station);

        Incident incident = new Incident();
        incident.setId(id);
        incident.setDescription("Ascenseur bloqué");
        incident.setDateOuverture(MAINTENANT.minus(Duration.ofHours(10)));
        incident.setStatut(StatutIncident.NON_AFFECTE);
        incident.setEquipement(equipement);
        return incident;
    }

    private Incident incidentResolu(Long id) {
        Incident incident = incidentNonAffecte(id);
        incident.setPrestataire(prestataire(3L));
        incident.setStatut(StatutIncident.RESOLU);
        incident.setDateResolution(MAINTENANT.minus(Duration.ofHours(1)));
        return incident;
    }

    private Prestataire prestataire(Long id) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        prestataire.setNom("Prestataire " + id);
        return prestataire;
    }
}
