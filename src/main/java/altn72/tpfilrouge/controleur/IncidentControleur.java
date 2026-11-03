package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.AffecterPrestataireRequestDto;
import altn72.tpfilrouge.dto.DeclarerIncidentRequestDto;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.dto.ModifierDescriptionRequestDto;
import altn72.tpfilrouge.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/incident")
public class IncidentControleur {
    private final IncidentService incidentService;

    public IncidentControleur(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<IncidentDto> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    @GetMapping("/historique")
    public List<IncidentDto> getHistorique() {
        return incidentService.getHistoriqueIncidentsResolus();
    }

    @GetMapping("/{id}")
    public IncidentDto getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    @PostMapping
    public ResponseEntity<IncidentDto> creerIncident(@Valid @RequestBody DeclarerIncidentRequestDto incidentDto) {
        IncidentDto nouveauIncident = incidentService.creerIncident(incidentDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nouveauIncident);
    }

    @PatchMapping("/{id}/description")
    public IncidentDto modifierDescription(@PathVariable Long id, @Valid @RequestBody ModifierDescriptionRequestDto requete) {
        return incidentService.modifierDescription(id, requete.description());
    }

    @PatchMapping("/{id}/affectation")
    public IncidentDto affecterPrestataire(@PathVariable Long id, @Valid @RequestBody AffecterPrestataireRequestDto requete) {
        return incidentService.affecterPrestataire(id, requete.prestataireId());
    }

    @PatchMapping("/{id}/resolution")
    public IncidentDto resoudre(@PathVariable Long id) {
        return incidentService.resoudre(id);
    }
}
