package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.TableauDeBordDto;
import altn72.tpfilrouge.modele.NiveauAttention;
import altn72.tpfilrouge.service.TableauDeBordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TableauDeBordControleur {
    private final TableauDeBordService tableauDeBordService;

    public TableauDeBordControleur(TableauDeBordService tableauDeBordService) {
        this.tableauDeBordService = tableauDeBordService;
    }

    @GetMapping("/tableau-de-bord")
    public TableauDeBordDto getTableauDeBord(@RequestParam(required = false) Long stationId,
                                             @RequestParam(required = false) String typeEquipement,
                                             @RequestParam(required = false) NiveauAttention niveauAttention) {
        return tableauDeBordService.getTableauDeBord(stationId, typeEquipement, niveauAttention);
    }
}
