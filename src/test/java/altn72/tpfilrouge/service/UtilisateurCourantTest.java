package altn72.tpfilrouge.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;

class UtilisateurCourantTest {
    private final UtilisateurCourant utilisateurCourant = new UtilisateurCourant();

    @AfterEach
    void viderLaSession() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void sansUtilisateurConnecteLAuteurEstLeSysteme() {
        assertThat(utilisateurCourant.getLogin()).isEqualTo("Système");
    }

    @Test
    void lAuteurEstLeLoginDeLUtilisateurConnecte() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken("agent", null));

        assertThat(utilisateurCourant.getLogin()).isEqualTo("agent");
    }
}
