package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

/** PATCH /api/matriculas/{id}/pagar. El id sale de la memoria del actor, nunca fijo. */
public class PagarMatricula implements Task {

    private final String metodo;

    public PagarMatricula(String metodo) {
        this.metodo = metodo;
    }

    /** Sin cuerpo en la peticion: la API registra el metodo por defecto. */
    public static PagarMatricula sinCuerpo() {
        return new PagarMatricula(null);
    }

    public static PagarMatricula con(String metodo) {
        return new PagarMatricula(metodo);
    }

    @Override
    @Step("{0} paga su matricula")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        Long id = actor.recall("idMatricula");
        actor.attemptsTo(
                Patch.to("/api/matriculas/{id}/pagar")
                        .with(req -> {
                            req.pathParam("id", id).auth().oauth2(token);
                            if (metodo != null) {
                                req.contentType(ContentType.JSON).body(Map.of("metodo", metodo));
                            }
                            return req;
                        })
        );
    }
}
