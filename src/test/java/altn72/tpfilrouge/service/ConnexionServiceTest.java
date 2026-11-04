package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.ProfilUtilisateur;
import altn72.tpfilrouge.modele.Utilisateur;
import altn72.tpfilrouge.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConnexionServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @InjectMocks
    private ConnexionService connexionService;

    @Test
    void unLoginConnuRenvoieLUtilisateurAvecSonPrenomEtSonProfil() {
        when(utilisateurRepository.findByLogin("agent")).thenReturn(Optional.of(agent()));

        UtilisateurConnecte utilisateurConnecte = (UtilisateurConnecte) connexionService.loadUserByUsername("agent");

        assertThat(utilisateurConnecte.getUsername()).isEqualTo("agent");
        assertThat(utilisateurConnecte.getPrenom()).isEqualTo("Lucas");
        assertThat(utilisateurConnecte.getProfil()).isEqualTo(ProfilUtilisateur.AGENT_TERRAIN);
    }

    @Test
    void unLoginInconnuLeveUneException() {
        when(utilisateurRepository.findByLogin("inconnu")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> connexionService.loadUserByUsername("inconnu"))
                .isInstanceOf(UsernameNotFoundException.class);
    }

    private Utilisateur agent() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("agent");
        utilisateur.setMotDePasse("hache");
        utilisateur.setPrenom("Lucas");
        utilisateur.setProfil(ProfilUtilisateur.AGENT_TERRAIN);
        return utilisateur;
    }
}
