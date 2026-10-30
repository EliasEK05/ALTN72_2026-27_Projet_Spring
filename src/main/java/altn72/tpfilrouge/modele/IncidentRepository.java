package altn72.tpfilrouge.modele;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    // F3 Incidents d'un équipement, du plus récent au plus ancien
    List<Incident> findByEquipementIdOrderByDateOuvertureDesc(Long equipementId);
}
