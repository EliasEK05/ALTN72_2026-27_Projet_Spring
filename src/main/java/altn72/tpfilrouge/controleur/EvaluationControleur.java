package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.dto.EvaluationDto;
import altn72.tpfilrouge.dto.EvaluerPrestataireRequestDto;
import altn72.tpfilrouge.service.EvaluationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class EvaluationControleur {
    private final EvaluationService evaluationService;

    public EvaluationControleur(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @PostMapping("/incident/{id}/evaluation")
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluationDto evaluer(@PathVariable Long id, @Valid @RequestBody EvaluerPrestataireRequestDto requete) {
        return evaluationService.evaluer(id, requete.note(), requete.commentaire());
    }
}
