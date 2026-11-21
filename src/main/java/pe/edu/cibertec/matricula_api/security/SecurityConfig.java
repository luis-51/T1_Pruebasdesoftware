package pe.edu.cibertec.matricula_api.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static org.springframework.http.HttpMethod.*;

@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String DOCENTE = "DOCENTE";
    private static final String ALUMNO = "ALUMNO";

    @Bean
    SecurityFilterChain api(HttpSecurity http,
                            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) throws Exception {
        AuthenticationEntryPoint sinSesion = (req, res, ex) -> resolver.resolveException(req, res, null, ex);
        AccessDeniedHandler sinPermiso = (req, res, ex) -> resolver.resolveException(req, res, null, ex);

        http.csrf(c -> c.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/actuator/health").permitAll()
                        .requestMatchers(POST, "/api/auth/login", "/api/auth/refresh").permitAll()
                        .requestMatchers(GET, "/api/cursos/*/alumnos").hasAnyRole(ADMIN, DOCENTE)
                        .requestMatchers(GET, "/api/cursos", "/api/cursos/*", "/api/carreras", "/api/carreras/*",
                                "/api/periodos", "/api/periodos/*").permitAll()
                        .requestMatchers("/api/cursos/**", "/api/carreras/**", "/api/periodos/**", "/api/usuarios/**").hasRole(ADMIN)
                        .requestMatchers(GET, "/api/docentes/*", "/api/docentes/*/cursos").hasAnyRole(ADMIN, DOCENTE)
                        .requestMatchers("/api/docentes/**").hasRole(ADMIN)
                        .requestMatchers(GET, "/api/alumnos/*", "/api/alumnos/*/constancia", "/api/alumnos/*/historial")
                        .hasAnyRole(ADMIN, ALUMNO)
                        .requestMatchers("/api/alumnos/**").hasRole(ADMIN)
                        .requestMatchers(PATCH, "/api/matriculas/*/nota").hasAnyRole(ADMIN, DOCENTE)
                        .requestMatchers(GET, "/api/matriculas/*").authenticated()
                        .requestMatchers("/api/matriculas/**", "/api/pagos/**").hasAnyRole(ADMIN, ALUMNO)
                        .anyRequest().authenticated())
                .oauth2ResourceServer(o -> o
                        .jwt(j -> j.jwtAuthenticationConverter(conversor()))
                        .authenticationEntryPoint(sinSesion)
                        .accessDeniedHandler(sinPermiso))
                .exceptionHandling(e -> e.authenticationEntryPoint(sinSesion).accessDeniedHandler(sinPermiso));
        return http.build();
    }

    private JwtAuthenticationConverter conversor() {
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("rol");
        roles.setAuthorityPrefix("ROLE_");
        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(roles);
        return conversor;
    }
}
