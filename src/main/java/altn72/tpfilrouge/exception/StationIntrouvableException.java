package altn72.tpfilrouge.exception;

public class StationIntrouvableException extends RessourceIntrouvableException {
    public StationIntrouvableException(Long id) {
        super("La station n°" + id + " est introuvable");
    }
}
