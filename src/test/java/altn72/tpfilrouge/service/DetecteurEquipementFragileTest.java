package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.Station;
import altn72.tpfilrouge.modele.StatutIncident;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect")
class DetecteurEquipementFragileTest {
    private static final Instant MAINTENANT = Instant.parse("2026-11-01T12:00:00Z");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private IncidentRepository incidentRepository;

    private DetecteurEquipementFragile detecteur;
    private Equipement ascenseur;
    private Equipement portique;

    @BeforeEach
    void initialiser() {
        detecteur = new DetecteurEquipementFragile(incidentRepository, Clock.fixed(MAINTENANT, ZoneOffset.UTC));
        Station station = new Station();
        station.setId(1L);
        station.setNom("Châtelet");
        entityManager.persist(station);
        ascenseur = persisterEquipement(1L, "ASC-001", station);
        portique = persisterEquipement(2L, "POR-001", station);
    }

    @Test
    void deuxIncidentsDansLaFenetreNeSuffisentPas() {
        persisterIncidents(ascenseur, 2, Duration.ofDays(1), StatutIncident.NON_AFFECTE);

        assertThat(detecteur.estFragile(ascenseur.getId())).isFalse();
    }

    @Test
    void troisIncidentsDansLaFenetreRendentLEquipementFragile() {
        persisterIncidents(ascenseur, 3, Duration.ofDays(1), StatutIncident.NON_AFFECTE);

        assertThat(detecteur.estFragile(ascenseur.getId())).isTrue();
    }

    @Test
    void unIncidentResoluCompteAussi() {
        persisterIncidents(ascenseur, 1, Duration.ofDays(1), StatutIncident.AFFECTE);
        persisterIncidents(ascenseur, 1, Duration.ofDays(2), StatutIncident.NON_AFFECTE);
        persisterIncidents(ascenseur, 1, Duration.ofDays(3), StatutIncident.RESOLU);

        assertThat(detecteur.estFragile(ascenseur.getId())).isTrue();
    }

    @Test
    void unIncidentOuvertIlYa30JoursEt1MinuteNEstPlusCompte() {
        persisterIncidents(ascenseur, 2, Duration.ofDays(1), StatutIncident.NON_AFFECTE);
        persisterIncidents(ascenseur, 1, Duration.ofDays(30).plusMinutes(1), StatutIncident.NON_AFFECTE);

        assertThat(detecteur.estFragile(ascenseur.getId())).isFalse();
    }

    @Test
    void unIncidentOuvertIlYa29Jours23Heures59EstCompte() {
        persisterIncidents(ascenseur, 2, Duration.ofDays(1), StatutIncident.NON_AFFECTE);
        persisterIncidents(ascenseur, 1, Duration.ofDays(30).minusMinutes(1), StatutIncident.NON_AFFECTE);

        assertThat(detecteur.estFragile(ascenseur.getId())).isTrue();
    }

    @Test
    void unIncidentOuvertIlYa30JoursPileEstCompte() {
        persisterIncidents(ascenseur, 2, Duration.ofDays(1), StatutIncident.NON_AFFECTE);
        persisterIncidents(ascenseur, 1, Duration.ofDays(30), StatutIncident.NON_AFFECTE);

        assertThat(detecteur.estFragile(ascenseur.getId())).isTrue();
    }

    @Test
    void lesIncidentsVieuxDe45JoursNeComptentPas() {
        persisterIncidents(ascenseur, 2, Duration.ofDays(1), StatutIncident.NON_AFFECTE);
        persisterIncidents(ascenseur, 5, Duration.ofDays(45), StatutIncident.RESOLU);

        assertThat(detecteur.estFragile(ascenseur.getId())).isFalse();
    }

    @Test
    void lesIncidentsDUnEquipementNeRendentPasSonVoisinFragile() {
        persisterIncidents(ascenseur, 3, Duration.ofDays(1), StatutIncident.NON_AFFECTE);
        persisterIncidents(portique, 1, Duration.ofDays(1), StatutIncident.NON_AFFECTE);

        assertThat(detecteur.estFragile(ascenseur.getId())).isTrue();
        assertThat(detecteur.estFragile(portique.getId())).isFalse();
    }

    private Equipement persisterEquipement(Long id, String reference, Station station) {
        Equipement equipement = new Equipement();
        equipement.setId(id);
        equipement.setReference(reference);
        equipement.setLibelle("Équipement " + reference);
        equipement.setType("Ascenseur");
        equipement.setEtatFonctionnement("En panne");
        equipement.setStation(station);
        return entityManager.persist(equipement);
    }

    private void persisterIncidents(Equipement equipement, int nombre, Duration ouvertIlYa, StatutIncident statut) {
        for (int i = 0; i < nombre; i++) {
            Incident incident = new Incident();
            incident.setEquipement(equipement);
            incident.setDescription("Panne");
            incident.setStatut(statut);
            incident.setDateOuverture(MAINTENANT.minus(ouvertIlYa));
            if (statut == StatutIncident.RESOLU) {
                incident.setDateResolution(MAINTENANT);
            }
            entityManager.persist(incident);
        }
    }
}
