package altn72.tpfilrouge.service;

import altn72.tpfilrouge.repository.IncidentRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

// Un équipement est fragile s'il a eu au moins 3 incidents ouverts sur les 30 derniers jours, quel que soit leur statut.
@Component
public class DetecteurEquipementFragile {
    private static final long SEUIL_INCIDENTS = 3;
    private static final Duration FENETRE = Duration.ofDays(30);

    private final IncidentRepository incidentRepository;
    private final Clock clock;

    public DetecteurEquipementFragile(IncidentRepository incidentRepository, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.clock = clock;
    }

    public boolean estFragile(Long equipementId) {
        Instant debutFenetre = Instant.now(clock).minus(FENETRE);
        return incidentRepository.countByEquipementIdAndDateOuvertureGreaterThanEqual(equipementId, debutFenetre) >= SEUIL_INCIDENTS;
    }
}
