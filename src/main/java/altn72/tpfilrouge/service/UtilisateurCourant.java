package altn72.tpfilrouge.service;

import org.springframework.stereotype.Component;

@Component
public class UtilisateurCourant {
    private static final String AUTEUR_PAR_DEFAUT = "Système";

    // Tant que la connexion (F1) n'existe pas, aucun utilisateur n'est identifié : seul ce composant changera ensuite
    public String getLogin() {
        return AUTEUR_PAR_DEFAUT;
    }
}
