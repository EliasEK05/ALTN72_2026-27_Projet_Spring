package altn72.tpfilrouge.exception;

import altn72.tpfilrouge.dto.ErreurDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
public class GestionnaireExceptions {
    private static final Logger LOGGER = LoggerFactory.getLogger(GestionnaireExceptions.class);

    @ExceptionHandler(RessourceIntrouvableException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErreurDto gererRessourceIntrouvable(RessourceIntrouvableException exception) {
        return new ErreurDto(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler({TransitionIncidentInterditeException.class, EvaluationImpossibleException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErreurDto gererOperationInterdite(RuntimeException exception) {
        return new ErreurDto(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    // RG11 Action interdite pour le profil de l'utilisateur connecté
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErreurDto gererAccesRefuse() {
        return new ErreurDto(HttpStatus.FORBIDDEN.value(), "Votre profil ne vous permet pas de réaliser cette action");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurDto gererDonneesInvalides(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(" ; "));
        return new ErreurDto(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurDto gererParametreInvalide(MethodArgumentTypeMismatchException exception) {
        return new ErreurDto(HttpStatus.BAD_REQUEST.value(),
                "La valeur « " + exception.getValue() + " » n'est pas valide pour le paramètre " + exception.getName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErreurDto gererCorpsIllisible() {
        return new ErreurDto(HttpStatus.BAD_REQUEST.value(), "Le corps de la requête est absent ou mal formé");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErreurDto gererErreurInattendue(Exception exception) {
        LOGGER.error("Erreur inattendue", exception);
        return new ErreurDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Une erreur inattendue est survenue");
    }
}
