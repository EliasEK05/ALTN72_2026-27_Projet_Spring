package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.mapper.IncidentMapper;
import altn72.tpfilrouge.modele.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class IncidentService {
    private final IncidentRepository incidentRepository;
    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;
    private final IncidentMapper incidentMapper;
    private final Clock clock;

    public IncidentService(IncidentRepository incidentRepository, EquipementRepository equipementRepository, StationRepository stationRepository,  IncidentMapper incidentMapper,  Clock clock) {
        this.incidentRepository = incidentRepository;
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
        this.incidentMapper = incidentMapper;
        this.clock = clock;
    }

    @Transactional
    public IncidentDto creerIncident(DeclarerIncidentRequestDto requete) {
        Equipement equipement = equipementRepository.findById(requete.equipementId())
                .orElseThrow(()->new RuntimeException("Equipement introuvable"));

        Station station = equipement.getStation();

        Incident incident= incidentMapper.declarerIncidentRequestToEntity(requete, equipement, Instant.now(clock), station);
        return incidentMapper.toIncidentDto(incidentRepository.save(incident));
    }

    public Optional<IncidentDto> getIncidentById(Long id) {
        Incident incident = incidentRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Incident introuvable"));

        return Optional.of(incidentMapper.toIncidentDto(incident));
    }

    @Transactional
    public Optional<IncidentDto> modifierIncident(Long id, Incident incident) {
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

            return Optional.of(incidentMapper.toIncidentDto(existingIncident));
        }
        return  Optional.empty();
    }

    public Optional<List<IncidentDto>> getAllIncidents() {
        List<IncidentDto> incidents = incidentRepository.findAllByOrderByDateOuvertureDesc()
                .stream()
                .map(incidentMapper::toIncidentDto)
                .toList();
        return Optional.of(incidents);
    }
}
