package altn72.tpfilrouge.exception;

public class IncidentIntrouvableException extends RessourceIntrouvableException {
    public IncidentIntrouvableException(Long id) {
        super("L'incident n°" + id + " est introuvable");
    }
}
