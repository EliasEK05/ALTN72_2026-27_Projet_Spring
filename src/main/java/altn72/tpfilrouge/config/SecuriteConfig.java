package altn72.tpfilrouge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecuriteConfig {

    // F1 Pages et API réservées aux utilisateurs connectés (§7.7)
    @Bean
    public SecurityFilterChain filtreDeSecurite(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(autorisations -> autorisations
                        .requestMatchers("/connexion", "/error").permitAll()
                        .anyRequest().authenticated())
                .formLogin(formulaire -> formulaire
                        .loginPage("/connexion")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/connexion?erreur"))
                .logout(deconnexion -> deconnexion
                        .logoutSuccessUrl("/connexion?deconnexion"))
                .httpBasic(Customizer.withDefaults())
                // CSRF désactivé pour que l'API REST reste appelable hors navigateur
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public PasswordEncoder encodeurDeMotDePasse() {
        return new BCryptPasswordEncoder();
    }
}
