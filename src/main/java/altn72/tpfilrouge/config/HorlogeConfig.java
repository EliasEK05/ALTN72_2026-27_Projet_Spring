package altn72.tpfilrouge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class HorlogeConfig {

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
