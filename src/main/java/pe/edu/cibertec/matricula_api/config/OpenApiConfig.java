package pe.edu.cibertec.matricula_api.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI matriculaOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Matrícula CIBERTEC API")
                        .version("2.0")
                        .description("""
                                API de matrícula usada como sistema bajo prueba en el curso Pruebas de Software.
                                Inicie sesión en POST /api/auth/login, copie el accessToken y péguelo en Authorize.
                                Reglas: S/ 120 por crédito, 20% de descuento con promedio >= 17 y 10% con >= 15,
                                máximo 24 créditos por periodo, nota aprobatoria 13."""))
                .components(new Components().addSecuritySchemes("bearer", new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearer"));
    }
}
