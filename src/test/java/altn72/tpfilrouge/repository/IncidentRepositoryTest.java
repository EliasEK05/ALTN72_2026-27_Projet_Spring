package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.modele.StatutIncident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class IncidentRepositoryTest {
    private static final Instant MAINTENANT = Instant.parse("2026-11-01T12:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IncidentRepository incidentRepository;

    private Equipement ascenseurChatelet;
    private Equipement portiqueNation;

    @BeforeEach
    void initialiser() {
        Station chatelet = persisterStation(1L, "Châtelet");
        Station nation = persisterStation(2L, "Nation");
        ascenseurChatelet = persisterEquipement(1L, "ASC-001", "Ascenseur", chatelet);
        portiqueNation = persisterEquipement(2L, "POR-001", "Portique", nation);
    }

    @Test
    void unIncidentResoluNApparaitPasParmiLesIncidentsActifs() {
        Incident actif = persisterIncident(ascenseurChatelet, StatutIncident.NON_AFFECTE, 10, null);
        persisterIncident(ascenseurChatelet, StatutIncident.RESOLU, 20, 5);

        List<Incident> incidentsActifs = incidentRepository.findIncidentsActifs(null, null);

        assertThat(incidentsActifs).containsExactly(actif);
    }

    @Test
    void lesIncidentsActifsSontDuPlusAncienAuPlusRecent() {
        Incident recent = persisterIncident(ascenseurChatelet, StatutIncident.NON_AFFECTE, 2, null);
        Incident ancien = persisterIncident(portiqueNation, StatutIncident.AFFECTE, 50, null);

        assertThat(incidentRepository.findIncidentsActifs(null, null)).containsExactly(ancien, recent);
    }

    @Test
    void lesIncidentsActifsPeuventEtreFiltresParStation() {
        Incident aChatelet = persisterIncident(ascenseurChatelet, StatutIncident.NON_AFFECTE, 10, null);
        persisterIncident(portiqueNation, StatutIncident.NON_AFFECTE, 10, null);

        assertThat(incidentRepository.findIncidentsActifs(1L, null)).containsExactly(aChatelet);
    }

    @Test
    void lesIncidentsActifsPeuventEtreFiltresParTypeDEquipement() {
        persisterIncident(ascenseurChatelet, StatutIncident.NON_AFFECTE, 10, null);
        Incident surPortique = persisterIncident(portiqueNation, StatutIncident.NON_AFFECTE, 10, null);

        assertThat(incidentRepository.findIncidentsActifs(null, "Portique")).containsExactly(surPortique);
    }

    @Test
    void lHistoriqueEstOrdonneDuPlusRecentAuPlusAncienParDateDeResolution() {
        Incident resoluIlYa30Heures = persisterIncident(ascenseurChatelet, StatutIncident.RESOLU, 100, 30);
        Incident resoluIlYa1Heure = persisterIncident(ascenseurChatelet, StatutIncident.RESOLU, 40, 1);
        Incident resoluIlYa10Heures = persisterIncident(portiqueNation, StatutIncident.RESOLU, 60, 10);

        assertThat(incidentRepository.findByStatutOrderByDateResolutionDesc(StatutIncident.RESOLU))
                .containsExactly(resoluIlYa1Heure, resoluIlYa10Heures, resoluIlYa30Heures);
    }

    @Test
    void lesIncidentsActifsSontComptesParStation() {
        persisterIncident(ascenseurChatelet, StatutIncident.NON_AFFECTE, 10, null);
        persisterIncident(ascenseurChatelet, StatutIncident.AFFECTE, 10, null);
        persisterIncident(portiqueNation, StatutIncident.NON_AFFECTE, 10, null);
        persisterIncident(portiqueNation, StatutIncident.RESOLU, 10, 1);

        List<IncidentsActifsParStation> resultat = incidentRepository.compterIncidentsActifsParStation();

        assertThat(resultat).extracting(IncidentsActifsParStation::getStationNom).containsExactly("Châtelet", "Nation");
        assertThat(resultat).extracting(IncidentsActifsParStation::getNombreIncidents).containsExactly(2L, 1L);
    }

    private Station persisterStation(Long id, String nom) {
        Station station = new Station();
        station.setId(id);
        station.setNom(nom);
        return entityManager.persist(station);
    }

    private Equipement persisterEquipement(Long id, String reference, String type, Station station) {
        Equipement equipement = new Equipement();
        equipement.setId(id);
        equipement.setReference(reference);
        equipement.setLibelle(type + " " + reference);
        equipement.setType(type);
        equipement.setEtatFonctionnement("En panne");
        equipement.setStation(station);
        return entityManager.persist(equipement);
    }

    private Incident persisterIncident(Equipement equipement, StatutIncident statut, long ouvertIlYaHeures, Integer resoluIlYaHeures) {
        Incident incident = new Incident();
        incident.setEquipement(equipement);
        incident.setDescription("Panne");
        incident.setStatut(statut);
        incident.setDateOuverture(MAINTENANT.minus(Duration.ofHours(ouvertIlYaHeures)));
        if (resoluIlYaHeures != null) {
            incident.setDateResolution(MAINTENANT.minus(Duration.ofHours(resoluIlYaHeures)));
        }
        return entityManager.persist(incident);
    }
}
