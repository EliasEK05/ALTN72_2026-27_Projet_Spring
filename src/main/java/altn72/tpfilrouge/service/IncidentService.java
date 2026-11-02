package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.mapper.IncidentMapper;
import altn72.tpfilrouge.modele.*;
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
    private final PrestataireRepository prestataireRepository;
    private final IncidentMapper incidentMapper;
    private final CalculateurNiveauAttention calculateurNiveauAttention;
    private final Clock clock;

    public IncidentService(IncidentRepository incidentRepository, EquipementRepository equipementRepository, PrestataireRepository prestataireRepository, IncidentMapper incidentMapper, CalculateurNiveauAttention calculateurNiveauAttention, Clock clock) {
        this.incidentRepository = incidentRepository;
        this.equipementRepository = equipementRepository;
        this.prestataireRepository = prestataireRepository;
        this.incidentMapper = incidentMapper;
        this.calculateurNiveauAttention = calculateurNiveauAttention;
        this.clock = clock;
    }

    @Transactional
    public IncidentDto creerIncident(DeclarerIncidentRequestDto requete) {
        Equipement equipement = equipementRepository.findById(requete.equipementId())
                .orElseThrow(()->new RuntimeException("Equipement introuvable"));

        Incident incident= incidentMapper.declarerIncidentRequestToEntity(requete, equipement, Instant.now(clock));
        return versDto(incidentRepository.save(incident));
    }

    @Transactional
    public Optional<IncidentDto> getIncidentById(Long id) {
        return Optional.of(versDto(trouverIncident(id)));
    }

    @Transactional
    public Optional<List<IncidentDto>> getAllIncidents() {
        List<IncidentDto> incidents = incidentRepository.findAllByOrderByDateOuvertureDesc()
                .stream()
                .map(this::versDto)
                .toList();
        return Optional.of(incidents);
    }

    @Transactional
    public List<IncidentDto> getHistoriqueIncidentsResolus() {
        return incidentRepository.findByStatutOrderByDateResolutionDesc(StatutIncident.RESOLU)
                .stream()
                .map(this::versDto)
                .toList();
    }


    @Transactional
    public IncidentDto modifierDescription(Long incidentId, String description) {
        Incident incident = trouverIncident(incidentId);
        verifierNonResolu(incident);

        incident.setDescription(description);
        return versDto(incident);
    }

    @Transactional
    public IncidentDto affecterPrestataire(Long incidentId, Long prestataireId) {
        Incident incident = trouverIncident(incidentId);
        verifierNonResolu(incident);
        Prestataire prestataire = prestataireRepository.findById(prestataireId)
                .orElseThrow(() -> new RuntimeException("Prestataire introuvable"));

        incident.setPrestataire(prestataire);
        incident.setStatut(StatutIncident.AFFECTE);
        return versDto(incident);
    }

    @Transactional
    public IncidentDto resoudre(Long incidentId) {
        Incident incident = trouverIncident(incidentId);
        verifierNonResolu(incident);
        Instant dateResolution = Instant.now(clock);
        if (dateResolution.isBefore(incident.getDateOuverture())) {
            throw new RuntimeException("La date de résolution ne peut pas précéder la date d'ouverture");
        }

        incident.setDateResolution(dateResolution);
        incident.setStatut(StatutIncident.RESOLU);
        return versDto(incident);
    }

    private Incident trouverIncident(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident introuvable"));
    }

    private void verifierNonResolu(Incident incident) {
        if (incident.getStatut() == StatutIncident.RESOLU) {
            throw new RuntimeException("L'incident est résolu et ne peut plus être modifié");
        }
    }

    private IncidentDto versDto(Incident incident) {
        return incidentMapper.toIncidentDto(
                incident,
                calculateurNiveauAttention.calculerDureeOuverture(incident),
                calculateurNiveauAttention.calculerNiveauAttention(incident));
    }
}
