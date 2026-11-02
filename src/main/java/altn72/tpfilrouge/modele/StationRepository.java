package altn72.tpfilrouge.modele;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findAllByOrderByNomAsc();
}
