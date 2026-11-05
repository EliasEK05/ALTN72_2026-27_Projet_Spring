package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.EvenementIncidentDto;
import altn72.tpfilrouge.exception.IncidentIntrouvableException;
import altn72.tpfilrouge.modele.EvenementIncident;
import altn72.tpfilrouge.repository.EvenementIncidentRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JournalService {
    private final EvenementIncidentRepository evenementIncidentRepository;
    private final IncidentRepository incidentRepository;

    public JournalService(EvenementIncidentRepository evenementIncidentRepository, IncidentRepository incidentRepository) {
        this.evenementIncidentRepository = evenementIncidentRepository;
        this.incidentRepository = incidentRepository;
    }

    // Historique horodaté des changements apportés à un incident
    public List<EvenementIncidentDto> getJournal(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new IncidentIntrouvableException(incidentId);
        }
        return evenementIncidentRepository.findByIncidentIdOrderByDateEvenementAsc(incidentId)
                .stream()
                .map(this::versDto)
                .toList();
    }

    private EvenementIncidentDto versDto(EvenementIncident evenement) {
        return new EvenementIncidentDto(
                evenement.getId(),
                evenement.getType(),
                evenement.getDetails(),
                evenement.getAuteur(),
                evenement.getDateEvenement());
    }
}
