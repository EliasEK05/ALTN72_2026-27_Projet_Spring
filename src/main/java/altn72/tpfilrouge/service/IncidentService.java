package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public List<Incident> getIncentsByStationId(Long stationId) {
        return incidentRepository.findByEquipementIdOrderByDateOuvertureDesc(stationId);
    }
}
