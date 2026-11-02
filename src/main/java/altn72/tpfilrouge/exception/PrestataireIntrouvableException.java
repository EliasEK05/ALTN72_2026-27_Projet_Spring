package altn72.tpfilrouge.exception;

public class PrestataireIntrouvableException extends RessourceIntrouvableException {
    public PrestataireIntrouvableException(Long id) {
        super("Le prestataire n°" + id + " est introuvable");
    }
}
