package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.FicheEquipement;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.exception.EquipementIntrouvableException;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.modele.EquipementRepository;
import altn72.tpfilrouge.modele.StationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class EquipementService {
    //Injection de dépendance via constructeur
    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;
    private final IncidentService incidentService;

    public EquipementService(EquipementRepository equipementRepository, StationRepository stationRepository,
                             IncidentService incidentService) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
        this.incidentService = incidentService;
    }

    // F2 Récupère les équipements d'une station donnée
    public List<Equipement> getEquipements(Long stationId) {
        if (!stationRepository.existsById(stationId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station " + stationId + " introuvable");
        }
        return equipementRepository.findByStationId(stationId);
    }

    // F3 Récupère la fiche d'un équipement avec ses incidents
    public FicheEquipement getFicheEquipement(Long equipementId) {
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementIntrouvableException(equipementId));

        List<IncidentDto> incidents = incidentService.getIncidentsByEquipement(equipementId);

        return new FicheEquipement(equipement.getId(), equipement.getReference(), equipement.getLibelle(),
                                   equipement.getType(), equipement.getEtatFonctionnement(), incidents);
    }

}
