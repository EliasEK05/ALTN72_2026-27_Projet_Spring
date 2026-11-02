package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.modele.Prestataire;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateurNiveauAttentionTest {
    private static final Instant MAINTENANT = Instant.parse("2026-11-01T12:00:00Z");

    private final CalculateurNiveauAttention calculateur =
            new CalculateurNiveauAttention(Clock.fixed(MAINTENANT, ZoneOffset.UTC));

    @Test
    void unIncidentNonAffecteOuvertDepuis47HeuresEstVert() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(47));

        assertThat(calculateur.calculerNiveauAttention(incident)).isEqualTo(NiveauAttention.VERT);
    }

    @Test
    void unIncidentNonAffecteOuvertDepuis48HeuresPileEstOrange() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(48));

        assertThat(calculateur.calculerNiveauAttention(incident)).isEqualTo(NiveauAttention.ORANGE);
    }

    @Test
    void unIncidentNonAffecteOuvertDepuis71HeuresEstOrange() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(71));

        assertThat(calculateur.calculerNiveauAttention(incident)).isEqualTo(NiveauAttention.ORANGE);
    }

    @Test
    void unIncidentNonAffecteOuvertDepuis72HeuresPileEstRouge() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(72));

        assertThat(calculateur.calculerNiveauAttention(incident)).isEqualTo(NiveauAttention.ROUGE);
    }

    @Test
    void unIncidentAffecteNePorteJamaisDeCouleur() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(100));
        incident.setPrestataire(new Prestataire());

        assertThat(calculateur.calculerNiveauAttention(incident)).isEqualTo(NiveauAttention.AFFECTE);
    }

    @Test
    void laDureeOuvertureEstCalculeeJusquaMaintenant() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(5));

        assertThat(calculateur.calculerDureeOuverture(incident)).isEqualTo(Duration.ofHours(5));
    }

    @Test
    void laDureeOuvertureDUnIncidentResoluSArreteALaResolution() {
        Incident incident = incidentOuvertIlYa(Duration.ofHours(10));
        incident.setDateResolution(MAINTENANT.minus(Duration.ofHours(4)));

        assertThat(calculateur.calculerDureeOuverture(incident)).isEqualTo(Duration.ofHours(6));
    }

    private Incident incidentOuvertIlYa(Duration duree) {
        Incident incident = new Incident();
        incident.setDateOuverture(MAINTENANT.minus(duree));
        return incident;
    }
}
