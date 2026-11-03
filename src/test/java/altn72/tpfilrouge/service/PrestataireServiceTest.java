package altn72.tpfilrouge.service;

import altn72.tpfilrouge.dto.PrestataireDto;
import altn72.tpfilrouge.modele.Prestataire;
import altn72.tpfilrouge.repository.EvaluationRepository;
import altn72.tpfilrouge.repository.PrestataireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PrestataireServiceTest {

    @Mock
    private PrestataireRepository prestataireRepository;
    @Mock
    private EvaluationRepository evaluationRepository;

    @InjectMocks
    private PrestataireService prestataireService;

    @Test
    void chaquePrestataireEstRenvoyeAvecSaNoteMoyenne() {
        when(prestataireRepository.findAllByOrderByNomAsc()).thenReturn(List.of(prestataire(1L, "Kone"), prestataire(2L, "Otis")));
        when(evaluationRepository.calculerNoteMoyenne(1L)).thenReturn(4.5);
        when(evaluationRepository.calculerNoteMoyenne(2L)).thenReturn(null);

        List<PrestataireDto> prestataires = prestataireService.getPrestataires();

        assertThat(prestataires).extracting(PrestataireDto::noteMoyenne).containsExactly(4.5, null);
    }

    private Prestataire prestataire(Long id, String nom) {
        Prestataire prestataire = new Prestataire();
        prestataire.setId(id);
        prestataire.setNom(nom);
        return prestataire;
    }
}
