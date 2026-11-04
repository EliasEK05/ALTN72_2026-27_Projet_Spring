package altn72.tpfilrouge.controleur;

import altn72.tpfilrouge.service.UtilisateurConnecte;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// Met l'utilisateur connecté à disposition de toutes les pages, pour l'en-tête « Bonjour [Prénom] ([profil]) » (F1)
@ControllerAdvice
public class UtilisateurConnecteAdvice {

    @ModelAttribute("utilisateurConnecte")
    public UtilisateurConnecte utilisateurConnecte(@AuthenticationPrincipal UtilisateurConnecte utilisateurConnecte) {
        return utilisateurConnecte;
    }
}
