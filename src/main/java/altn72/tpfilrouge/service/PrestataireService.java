package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.PrestataireDto;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrestataireService {
    private final PrestataireRepository prestataireRepository;
    private final EvaluationRepository evaluationRepository;

    public PrestataireService(PrestataireRepository prestataireRepository, EvaluationRepository evaluationRepository) {
        this.prestataireRepository = prestataireRepository;
        this.evaluationRepository = evaluationRepository;
    }

    public List<PrestataireDto> getPrestataires() {
        return prestataireRepository.findAllByOrderByNomAsc()
                .stream()
                .map(this::versDto)
                .toList();
    }

    private PrestataireDto versDto(Prestataire prestataire) {
        return new PrestataireDto(
                prestataire.getId(),
                prestataire.getNom(),
                prestataire.getContact(),
                evaluationRepository.calculerNoteMoyenne(prestataire.getId()));
    }
}
