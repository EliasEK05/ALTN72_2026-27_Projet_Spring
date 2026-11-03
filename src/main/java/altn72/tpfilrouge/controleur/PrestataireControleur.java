package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.PrestataireDto;
import altn72.tpfilrouge.service.PrestataireService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PrestataireControleur {
    private final PrestataireService prestataireService;

    public PrestataireControleur(PrestataireService prestataireService) {
        this.prestataireService = prestataireService;
    }

    @GetMapping("/prestataire")
    public List<PrestataireDto> getPrestataires() {
        return prestataireService.getPrestataires();
    }
}
