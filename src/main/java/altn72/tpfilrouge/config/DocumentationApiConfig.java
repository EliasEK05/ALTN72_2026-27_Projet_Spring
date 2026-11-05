package altn72.tpfilrouge.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DocumentationApiConfig {

    //Documentation de l'API REST
    @Bean
    public OpenAPI documentationApi() {
        return new OpenAPI().info(new Info()
                .title("API Supervision des équipements")
                .description("Consultation et suivi des incidents du réseau. Accès réservé aux utilisateurs connectés.")
                .version("1.0"));
    }
}
