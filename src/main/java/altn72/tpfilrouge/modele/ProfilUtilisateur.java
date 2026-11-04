package altn72.tpfilrouge.modele;

public enum ProfilUtilisateur {
    RESPONSABLE_EXPLOITATION("Responsable d'exploitation"),
    AGENT_TERRAIN("Agent de terrain");

    private final String libelle;

    ProfilUtilisateur(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
