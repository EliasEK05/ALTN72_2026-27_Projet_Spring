package altn72.tpfilrouge.exception;

public class EquipementIntrouvableException extends RessourceIntrouvableException {
    public EquipementIntrouvableException(Long id) {
        super("L'équipement n°" + id + " est introuvable");
    }
}
