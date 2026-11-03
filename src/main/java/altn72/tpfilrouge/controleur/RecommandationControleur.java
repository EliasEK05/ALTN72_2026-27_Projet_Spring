package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.RecommandationPrestataireDto;
import altn72.tpfilrouge.service.RecommandationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RecommandationControleur {
    private final RecommandationService recommandationService;

    public RecommandationControleur(RecommandationService recommandationService) {
        this.recommandationService = recommandationService;
    }

    @GetMapping("/incident/{id}/recommandation")
    public List<RecommandationPrestataireDto> recommanderPrestataires(@PathVariable Long id) {
        return recommandationService.recommanderPrestataires(id);
    }
}
