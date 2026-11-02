package altn72.tpfilrouge.exception;

public abstract class RessourceIntrouvableException extends RuntimeException {
    protected RessourceIntrouvableException(String message) {
        super(message);
    }
}
