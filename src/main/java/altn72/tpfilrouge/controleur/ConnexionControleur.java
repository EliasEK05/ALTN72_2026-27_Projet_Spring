package altn72.tpfilrouge.controleur;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ConnexionControleur {

    // F1 Page de connexion
    @GetMapping("/connexion")
    public String afficherConnexion() {
        return "connexion";
    }
}
