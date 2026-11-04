package altn72.tpfilrouge.service;

import altn72.tpfilrouge.modele.ProfilUtilisateur;
import altn72.tpfilrouge.modele.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UtilisateurConnecte implements UserDetails {
    private final String login;
    private final String motDePasse;
    private final String prenom;
    private final ProfilUtilisateur profil;

    public UtilisateurConnecte(Utilisateur utilisateur) {
        this.login = utilisateur.getLogin();
        this.motDePasse = utilisateur.getMotDePasse();
        this.prenom = utilisateur.getPrenom();
        this.profil = utilisateur.getProfil();
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public String getPassword() {
        return motDePasse;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + profil.name()));
    }

    public String getPrenom() {
        return prenom;
    }

    public ProfilUtilisateur getProfil() {
        return profil;
    }

    public boolean estResponsable() {
        return profil == ProfilUtilisateur.RESPONSABLE_EXPLOITATION;
    }
}
