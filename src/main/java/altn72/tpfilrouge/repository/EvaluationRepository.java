package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByIncidentId(Long incidentId);

    @Query("select avg(e.note) from Evaluation e where e.incident.prestataire.id = :prestataireId")
    Double calculerNoteMoyenne(@Param("prestataireId") Long prestataireId);

    // Aide à l'affectation : note moyenne d'un prestataire sur un type d'équipement
    @Query("select avg(e.note) from Evaluation e where e.incident.prestataire.id = :prestataireId and e.incident.equipement.type = :typeEquipement")
    Double calculerNoteMoyenneParType(@Param("prestataireId") Long prestataireId, @Param("typeEquipement") String typeEquipement);
}
