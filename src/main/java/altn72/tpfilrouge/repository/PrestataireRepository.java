package altn72.tpfilrouge.repository;

import altn72.tpfilrouge.modele.Prestataire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrestataireRepository extends JpaRepository<Prestataire, Long> {
    List<Prestataire> findAllByOrderByNomAsc();

    // Aide à l'affectation : prestataires compétents sur un type d'équipement
    @Query("select p from Prestataire p join p.competences c where c = :typeEquipement")
    List<Prestataire> findByCompetence(@Param("typeEquipement") String typeEquipement);
}
