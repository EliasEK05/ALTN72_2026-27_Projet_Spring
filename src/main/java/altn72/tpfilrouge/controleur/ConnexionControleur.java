package altn72.tpfilrouge.controleur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// F1 Affiche la page de connexion ; la vérification du login et du mot de passe est faite par Spring Security
@Controller
public class ConnexionControleur {

    @GetMapping("/connexion")
    public String afficherConnexion() {
        return "connexion";
    }
}
