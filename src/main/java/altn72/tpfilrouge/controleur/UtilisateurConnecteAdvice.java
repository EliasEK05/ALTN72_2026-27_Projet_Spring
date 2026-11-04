package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.UtilisateurConnecte;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class UtilisateurConnecteAdvice {

    // F1 Utilisateur affiché dans l'en-tête de chaque page
    @ModelAttribute("utilisateurConnecte")
    public UtilisateurConnecte utilisateurConnecte(@AuthenticationPrincipal UtilisateurConnecte utilisateurConnecte) {
        return utilisateurConnecte;
    }
}
