package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.NiveauAttention;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Component
public class CalculateurNiveauAttention {
    private static final Duration SEUIL_ORANGE = Duration.ofHours(48);
    private static final Duration SEUIL_ROUGE = Duration.ofHours(72);

    private final Clock clock;

    public CalculateurNiveauAttention(Clock clock) {
        this.clock = clock;
    }

    public Duration calculerDureeOuverture(Incident incident) {
        Instant fin = incident.getDateResolution() != null
                ? incident.getDateResolution()
                : Instant.now(clock);
        return Duration.between(incident.getDateOuverture(), fin);
    }

    public NiveauAttention calculerNiveauAttention(Incident incident) {
        if (incident.getPrestataire() != null) {
            return NiveauAttention.AFFECTE;
        }
        Duration duree = calculerDureeOuverture(incident);
        if (duree.compareTo(SEUIL_ROUGE) >= 0) {
            return NiveauAttention.ROUGE;
        }
        if (duree.compareTo(SEUIL_ORANGE) >= 0) {
            return NiveauAttention.ORANGE;
        }
        return NiveauAttention.VERT;
    }
}
