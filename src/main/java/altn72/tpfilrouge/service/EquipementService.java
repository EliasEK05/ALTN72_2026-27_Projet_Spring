package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.EquipementDto;
import altn72.tpfilrouge.dto.FicheEquipement;
import altn72.tpfilrouge.dto.IncidentDto;
import altn72.tpfilrouge.exception.EquipementIntrouvableException;
import altn72.tpfilrouge.exception.StationIntrouvableException;
import altn72.tpfilrouge.mapper.EquipementMapper;
import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.repository.EquipementRepository;
import altn72.tpfilrouge.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipementService {
    private final EquipementRepository equipementRepository;
    private final StationRepository stationRepository;
    private final IncidentService incidentService;
    private final EquipementMapper equipementMapper;

    public EquipementService(EquipementRepository equipementRepository, StationRepository stationRepository,
                             IncidentService incidentService, EquipementMapper equipementMapper) {
        this.equipementRepository = equipementRepository;
        this.stationRepository = stationRepository;
        this.incidentService = incidentService;
        this.equipementMapper = equipementMapper;
    }

    // F2 Récupère les équipements d'une station donnée
    public List<EquipementDto> getEquipementsDeLaStation(Long stationId) {
        if (!stationRepository.existsById(stationId)) {
            throw new StationIntrouvableException(stationId);
        }
        return equipementRepository.findByStationId(stationId)
                .stream()
                .map(equipementMapper::toEquipementDto)
                .toList();
    }

    public List<String> getTypesEquipement() {
        return equipementRepository.findTypesEquipement();
    }

    // F3 Récupère la fiche d'un équipement avec ses incidents
    @Transactional
    public FicheEquipement getFicheEquipement(Long equipementId) {
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new EquipementIntrouvableException(equipementId));

        List<IncidentDto> incidents = incidentService.getIncidentsByEquipement(equipementId);

        return equipementMapper.toFicheEquipement(equipement, incidents);
    }
}
