package altn72.tpfilrouge.controleur.page;

import altn72.tpfilrouge.dto.EquipementDto;
import altn72.tpfilrouge.dto.StationDto;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ReseauPageControleur {
    private final StationService stationService;
    private final EquipementService equipementService;

    public ReseauPageControleur(StationService stationService, EquipementService equipementService) {
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    @GetMapping("/reseau")
    public String afficherReseau(Model model) {
        Map<StationDto, List<EquipementDto>> equipementsParStation = new LinkedHashMap<>();
        stationService.getStations()
                .forEach(station -> equipementsParStation.put(station, equipementService.getEquipementsDeLaStation(station.id())));
        model.addAttribute("equipementsParStation", equipementsParStation);
        return "reseau";
    }

    @GetMapping("/reseau/equipements/{id}")
    public String afficherFicheEquipement(@PathVariable Long id, Model model) {
        model.addAttribute("fiche", equipementService.getFicheEquipement(id));
        return "fiche-equipement";
    }
}
