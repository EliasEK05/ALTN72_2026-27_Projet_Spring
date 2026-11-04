package altn72.tpfilrouge.controleur.page;

import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.service.EquipementService;
import altn72.tpfilrouge.service.StationService;
import altn72.tpfilrouge.service.TableauDeBordService;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SupervisionPageControleur {
    private final TableauDeBordService tableauDeBordService;
    private final StationService stationService;
    private final EquipementService equipementService;

    public SupervisionPageControleur(TableauDeBordService tableauDeBordService, StationService stationService, EquipementService equipementService) {
        this.tableauDeBordService = tableauDeBordService;
        this.stationService = stationService;
        this.equipementService = equipementService;
    }

    // Un filtre laissé sur « Tous » est envoyé vide : il doit signifier « pas de filtre », pas « type égal à "" »
    @InitBinder
    public void filtresVidesSansEffet(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping("/supervision")
    public String afficherTableauDeBord(@RequestParam(required = false) Long stationId,
                                        @RequestParam(required = false) String typeEquipement,
                                        @RequestParam(required = false) NiveauAttention niveauAttention,
                                        Model model) {
        model.addAttribute("tableauDeBord", tableauDeBordService.getTableauDeBord(stationId, typeEquipement, niveauAttention));
        model.addAttribute("stations", stationService.getStations());
        model.addAttribute("typesEquipement", equipementService.getTypesEquipement());
        model.addAttribute("niveauxAttention", NiveauAttention.values());
        model.addAttribute("stationId", stationId);
        model.addAttribute("typeEquipement", typeEquipement);
        model.addAttribute("niveauAttention", niveauAttention);
        return "supervision";
    }
}
