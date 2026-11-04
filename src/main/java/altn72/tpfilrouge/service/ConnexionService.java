package altn72.tpfilrouge.service;

import altn72.tpfilrouge.repository.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security appelle ce service lors de la connexion (F1) pour retrouver l'utilisateur à partir de son login
@Service
public class ConnexionService implements UserDetailsService {
    private final UtilisateurRepository utilisateurRepository;

    public ConnexionService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return utilisateurRepository.findByLogin(login)
                .map(UtilisateurConnecte::new)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun utilisateur ne correspond au login " + login));
    }
}
