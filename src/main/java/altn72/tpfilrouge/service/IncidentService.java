package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.modele.IncidentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;

    public IncidentService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public Incident creerIncident(Incident incident) {
        return incidentRepository.save(incident);
    }

    public Optional<Incident> getIncidentById(Long id) {
        return incidentRepository.findById(id);
    }

    @Transactional
    public Optional<Incident> modifierIncident(Long id, Incident incident) {
        incident.setId(id);
        Optional<Incident> optionalIncident = incidentRepository.findById(id);

        if (optionalIncident.isPresent()) {
            Incident existingIncident = optionalIncident.get();

            if ("RESOLU".equals(existingIncident.getStatut())) {
                return Optional.empty();
            }

            if ("RESOLU".equals(incident.getStatut())) {
                incident.setDateResolution(Instant.now());
            }

            BeanUtils.copyProperties(incident, existingIncident);
            incidentRepository.save(existingIncident);

            return Optional.of(existingIncident);
        }
        return  Optional.empty();
    }

    public Optional<List<Incident>> getAllIncidents() {
        return Optional.ofNullable(incidentRepository.findAllByOrderByDateOuvertureDesc());
    }
}
