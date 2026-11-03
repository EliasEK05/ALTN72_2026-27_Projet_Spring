package altn72.tpfilrouge.evenement;

import altn72.tpfilrouge.modele.EvenementIncident;
import altn72.tpfilrouge.repository.EvenementIncidentRepository;
import altn72.tpfilrouge.repository.IncidentRepository;
import altn72.tpfilrouge.service.UtilisateurCourant;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class JournalListener {
    private final EvenementIncidentRepository evenementIncidentRepository;
    private final IncidentRepository incidentRepository;
    private final UtilisateurCourant utilisateurCourant;
    private final Clock clock;

    public JournalListener(EvenementIncidentRepository evenementIncidentRepository, IncidentRepository incidentRepository, UtilisateurCourant utilisateurCourant, Clock clock) {
        this.evenementIncidentRepository = evenementIncidentRepository;
        this.incidentRepository = incidentRepository;
        this.utilisateurCourant = utilisateurCourant;
        this.clock = clock;
    }

    // Écouteur synchrone : l'événement est enregistré dans la même transaction que l'action journalisée (RG10)
    @EventListener
    public void journaliser(IncidentModifieEvenement evenement) {
        EvenementIncident evenementIncident = new EvenementIncident();
        evenementIncident.setIncident(incidentRepository.getReferenceById(evenement.incidentId()));
        evenementIncident.setType(evenement.type());
        evenementIncident.setDetails(evenement.details());
        evenementIncident.setAuteur(utilisateurCourant.getLogin());
        evenementIncident.setDateEvenement(Instant.now(clock));

        evenementIncidentRepository.save(evenementIncident);
    }
}
