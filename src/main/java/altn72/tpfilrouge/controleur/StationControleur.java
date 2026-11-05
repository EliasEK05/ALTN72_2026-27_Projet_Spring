package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.EquipementDto;
import altn72.tpfilrouge.dto.StationDto;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StationControleur {
    private final StationService stationService;
    private final EquipementService equipementService;

    public StationControleur(StationService stationService, EquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    @GetMapping("/station")
    public List<StationDto> getStations() {
        return stationService.getStations();
    }

    // Liste des équipements de la station {id}
    @GetMapping("/station/{id}/equipements")
    public List<EquipementDto> getEquipementsDeLaStation(@PathVariable Long id) {
        return equipementService.getEquipementsDeLaStation(id);
    }
}
