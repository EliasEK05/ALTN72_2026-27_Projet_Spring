package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipementRepository extends JpaRepository<Equipement, Long> {
    // F2 Requête pour récupérer les équipements d'une station donnée
    List<Equipement> findByStationId(Long stationId);
}
