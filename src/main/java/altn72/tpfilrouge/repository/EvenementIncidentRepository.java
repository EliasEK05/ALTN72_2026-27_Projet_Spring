package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.EvenementIncident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementIncidentRepository extends JpaRepository<EvenementIncident, Long> {
    // FTN5 Journal d'un incident dans l'ordre chronologique
    List<EvenementIncident> findByIncidentIdOrderByDateEvenementAsc(Long incidentId);
}
