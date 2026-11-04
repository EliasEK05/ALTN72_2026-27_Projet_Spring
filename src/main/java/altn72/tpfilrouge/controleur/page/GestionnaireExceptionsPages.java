package altn72.tpfilrouge.controleur.page;

import altn72.tpfilrouge.exception.EvaluationImpossibleException;
import altn72.tpfilrouge.exception.RessourceIntrouvableException;
import altn72.tpfilrouge.exception.TransitionIncidentInterditeException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;

// Pendant pour les pages web de GestionnaireExceptions (API) : mêmes exceptions, mais un message lisible au lieu de JSON
@ControllerAdvice(basePackageClasses = GestionnaireExceptionsPages.class)
public class GestionnaireExceptionsPages {
    private static final Logger LOGGER = LoggerFactory.getLogger(GestionnaireExceptionsPages.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    public ModelAndView gererRessourceIntrouvable(RessourceIntrouvableException exception) {
        return pageErreur(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ModelAndView gererAccesRefuse() {
        return pageErreur(HttpStatus.FORBIDDEN, "Votre profil ne vous permet pas de réaliser cette action");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ModelAndView gererParametreInvalide(MethodArgumentTypeMismatchException exception) {
        return pageErreur(HttpStatus.BAD_REQUEST, "La valeur « " + exception.getValue() + " » n'est pas valide pour le paramètre " + exception.getName());
    }

    // Une action refusée sur un incident (POST /incidents/{id}/...) ramène sur sa page avec le message
    @ExceptionHandler({TransitionIncidentInterditeException.class, EvaluationImpossibleException.class})
    public String gererOperationInterdite(RuntimeException exception, HttpServletRequest requete) {
        RequestContextUtils.getOutputFlashMap(requete).put("erreur", exception.getMessage());
        String chemin = requete.getRequestURI().substring(requete.getContextPath().length());
        return "redirect:" + chemin.substring(0, chemin.lastIndexOf('/'));
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView gererErreurInattendue(Exception exception) {
        LOGGER.error("Erreur inattendue", exception);
        return pageErreur(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue");
    }

    private ModelAndView pageErreur(HttpStatus statut, String message) {
        ModelAndView page = new ModelAndView("erreur");
        page.setStatus(statut);
        page.addObject("message", message);
        return page;
    }
}
