package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    boolean existsByIncidentId(Long incidentId);

    @Query("select avg(e.note) from Evaluation e where e.incident.prestataire.id = :prestataireId")
    Double calculerNoteMoyenne(@Param("prestataireId") Long prestataireId);
}
