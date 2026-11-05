package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.FicheEquipement;
import altn72.tpfilrouge.service.EquipementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EquipementControleur {
    private final EquipementService equipementService;

    public EquipementControleur(EquipementService equipementService) {
        this.equipementService = equipementService;
    }

    // Consulter la fiche d'un équipement
    @GetMapping("/equipements/{id}")
    public FicheEquipement getFicheEquipement(@PathVariable Long id) {
        return equipementService.getFicheEquipement(id);
    }
}
