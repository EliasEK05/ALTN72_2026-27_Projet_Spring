package altn72.tpfilrouge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecuriteConfig {

    @Bean
    public SecurityFilterChain filtreDeSecurite(HttpSecurity http) throws Exception {
        http
                // Toutes les pages et l'API demandent d'être connecté, sauf la page de connexion (§7.7)
                .authorizeHttpRequests(autorisations -> autorisations
                        .requestMatchers("/connexion", "/error").permitAll()
                        .anyRequest().authenticated())
                // F1 : en cas d'erreur, on réaffiche la même page avec un message
                .formLogin(formulaire -> formulaire
                        .loginPage("/connexion")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/connexion?erreur"))
                .logout(deconnexion -> deconnexion
                        .logoutSuccessUrl("/connexion?deconnexion"))
                // L'API REST peut aussi être appelée avec login et mot de passe (requetes.http, Postman)
                .httpBasic(Customizer.withDefaults())
                // Désactivé : l'API REST est appelée hors navigateur, par des clients qui n'ont pas de jeton CSRF
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder encodeurDeMotDePasse() {
        return new BCryptPasswordEncoder();
    }
}
