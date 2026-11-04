package altn72.tpfilrouge.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurCourant {
    private static final String AUTEUR_PAR_DEFAUT = "Système";

    // Sans utilisateur connecté, l'action est attribuée au système
    public String getLogin() {
        Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
        if (authentification == null) {
            return AUTEUR_PAR_DEFAUT;
        }
        return authentification.getName();
    }
}
