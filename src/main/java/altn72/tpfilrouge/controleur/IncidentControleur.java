package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Incident;
import altn72.tpfilrouge.service.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/incident")
public class IncidentControleur {
    private final IncidentService incidentService;

    public IncidentControleur(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> getIncidentById(@PathVariable Long id) {
        Optional<Incident> incident = incidentService.getIncidentById(id);

        return incident
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Incident> creerIncident(@Valid @RequestBody Incident incident) {
        Incident nouveauIncident = incidentService.creerIncident(incident);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nouveauIncident);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> modifierIncident(@PathVariable Long id, @Valid @RequestBody Incident incident) {
        Optional<Incident> incidentModifie = incidentService.modifierIncident(id, incident);

        return incidentModifie
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
