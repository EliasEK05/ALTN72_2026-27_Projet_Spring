package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.modele.Equipement;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.StationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class StationControleur {
    //Injection de dépendance via constructeur
    private final EquipementService equipementService;

    public StationControleur(StationService stationService, EquipementService equipementService) {
        this.equipementService = equipementService;
    }

    // F2 Liste des équipements de la station {id}
    @GetMapping("/station/{id}/equipements")
    public List<Equipement> getEquipementById(@PathVariable Long id) {
        return equipementService.getEquipements(id);
    }

}
