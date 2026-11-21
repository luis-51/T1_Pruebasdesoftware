package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

/**
 * POST /api/auth/refresh con el refresh token que el actor recuerda bajo la clave indicada
 * ("refreshToken" = el vigente, "refreshTokenUsado" = el que ya se uso).
 * Solo si responde 200 reemplaza los tokens en la memoria del actor.
 */
public class RenovarToken implements Task {

    private final String claveDelRefresh;

    public RenovarToken(String claveDelRefresh) {
        this.claveDelRefresh = claveDelRefresh;
    }

    public static RenovarToken con(String claveDelRefresh) {
        return new RenovarToken(claveDelRefresh);
    }

    @Override
    @Step("{0} renueva sus tokens con el refresh guardado como #claveDelRefresh")
    public <T extends Actor> void performAs(T actor) {
        String refresh = actor.recall(claveDelRefresh);
        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refresh)))
        );
        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 200) {
            String accessAnterior = actor.recall("accessToken");
            actor.remember("accessTokenAnterior", accessAnterior);
            actor.remember("refreshTokenUsado", refresh);
            actor.remember("accessToken", respuesta.jsonPath().getString("accessToken"));
            actor.remember("refreshToken", respuesta.jsonPath().getString("refreshToken"));
        }
    }
}
