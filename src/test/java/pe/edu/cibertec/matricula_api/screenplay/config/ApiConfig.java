package pe.edu.cibertec.matricula_api.screenplay.config;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;

public final class ApiConfig {

    private ApiConfig() {
    }

    public static String baseUrl() {
        return EnvironmentSpecificConfiguration
                .from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getProperty("restapi.baseurl");
    }
}
