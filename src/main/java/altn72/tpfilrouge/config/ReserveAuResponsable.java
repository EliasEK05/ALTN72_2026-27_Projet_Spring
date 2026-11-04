package altn72.tpfilrouge.config;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// RG11 Action réservée au responsable d'exploitation, vérifiée côté serveur à chaque appel du service
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('RESPONSABLE_EXPLOITATION')")
public @interface ReserveAuResponsable {
}
