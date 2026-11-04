package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.config.SecuriteConfig;
import altn72.tpfilrouge.modele.ProfilUtilisateur;
import altn72.tpfilrouge.modele.Utilisateur;
import altn72.tpfilrouge.service.ConnexionService;
import altn72.tpfilrouge.service.UtilisateurConnecte;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest({ConnexionControleur.class, AccueilControleur.class})
@Import(SecuriteConfig.class)
class ConnexionControleurTest {
    private static final String MOT_DE_PASSE = "responsable123";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConnexionService connexionService;

    @Test
    void laPageDeConnexionEstAccessibleSansEtreConnecte() throws Exception {
        mockMvc.perform(get("/connexion"))
                .andExpect(status().isOk())
                .andExpect(view().name("connexion"));
    }

    @Test
    void unUtilisateurNonConnecteEstRedirigeVersLaConnexion() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/connexion"));
    }

    @Test
    void uneConnexionReussieOuvreLaSessionEtRedirigeVersLAccueil() throws Exception {
        when(connexionService.loadUserByUsername("responsable")).thenReturn(responsable());

        mockMvc.perform(formLogin("/connexion").user("responsable").password(MOT_DE_PASSE))
                .andExpect(redirectedUrl("/"))
                .andExpect(authenticated().withUsername("responsable"));
    }

    @Test
    void unMauvaisMotDePasseReafficheLaPageDeConnexion() throws Exception {
        when(connexionService.loadUserByUsername("responsable")).thenReturn(responsable());

        mockMvc.perform(formLogin("/connexion").user("responsable").password("mauvais"))
                .andExpect(redirectedUrl("/connexion?erreur"))
                .andExpect(unauthenticated());
    }

    @Test
    void laPageDeConnexionAfficheUnMessageEnCasDErreur() throws Exception {
        mockMvc.perform(get("/connexion").param("erreur", ""))
                .andExpect(content().string(containsString("Login ou mot de passe incorrect")));
    }

    @Test
    void lEnTeteAfficheLePrenomEtLeProfilDeLUtilisateurConnecte() throws Exception {
        mockMvc.perform(get("/").with(user(responsable())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bonjour Camille (Responsable d")))
                .andExpect(content().string(containsString("Déconnectez-vous")));
    }

    @Test
    void laDeconnexionFermeLaSessionEtAfficheLaPageDeConnexion() throws Exception {
        mockMvc.perform(logout())
                .andExpect(redirectedUrl("/connexion?deconnexion"))
                .andExpect(unauthenticated());
    }

    private UtilisateurConnecte responsable() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin("responsable");
        utilisateur.setMotDePasse(new BCryptPasswordEncoder().encode(MOT_DE_PASSE));
        utilisateur.setPrenom("Camille");
        utilisateur.setProfil(ProfilUtilisateur.RESPONSABLE_EXPLOITATION);
        return new UtilisateurConnecte(utilisateur);
    }
}
