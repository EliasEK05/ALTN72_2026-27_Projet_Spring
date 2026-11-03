package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.StatutIncident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    // F3 Incidents d'un équipement, du plus récent au plus ancien
    List<Incident> findByEquipementIdOrderByDateOuvertureDesc(Long equipementId);
    List<Incident> findAllByOrderByDateOuvertureDesc();

    List<Incident> findByStatutOrderByDateResolutionDesc(StatutIncident statut);

    // F11 Incidents actifs, filtrables par station et par type d'équipement
    @Query("""
            select i from Incident i
            join fetch i.equipement e
            join fetch e.station s
            left join fetch i.prestataire
            left join fetch i.evaluation
            where i.statut <> altn72.tpfilrouge.modele.StatutIncident.RESOLU
            and (:stationId is null or s.id = :stationId)
            and (:typeEquipement is null or e.type = :typeEquipement)
            order by i.dateOuverture asc
            """)
    List<Incident> findIncidentsActifs(@Param("stationId") Long stationId,
                                       @Param("typeEquipement") String typeEquipement);

    // F11 Nombre d'incidents actifs par station
    @Query(value = """
            SELECT s.nom AS stationNom, COUNT(i.id) AS nombreIncidents
            FROM incident i
            JOIN equipement e ON e.id = i.equipement_id
            JOIN station s ON s.id = e.station_id
            WHERE i.statut <> 'RESOLU'
            GROUP BY s.id, s.nom
            ORDER BY nombreIncidents DESC
            """, nativeQuery = true)
    List<IncidentsActifsParStation> compterIncidentsActifsParStation();

    // Aide à l'affectation : incidents d'un prestataire, en cours ou résolus sur un type d'équipement
    long countByPrestataireIdAndStatut(Long prestataireId, StatutIncident statut);

    long countByPrestataireIdAndEquipementTypeAndStatut(Long prestataireId, String typeEquipement, StatutIncident statut);
}
