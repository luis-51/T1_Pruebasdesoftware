package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import java.util.List;

/**
 * GET /api/matriculas?curso=XXXX con el token del alumno.
 * Guarda en la memoria el id de la matricula encontrada (nunca se escribe fijo).
 */
public class ConsultarMatriculaDelCurso implements Task {

    private final String curso;

    public ConsultarMatriculaDelCurso(String curso) {
        this.curso = curso;
    }

    public static ConsultarMatriculaDelCurso de(String curso) {
        return new ConsultarMatriculaDelCurso(curso);
    }

    @Override
    @Step("{0} consulta su matricula del curso #curso")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.queryParam("curso", curso).auth().oauth2(token))
        );
        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 200) {
            List<Object> encontradas = respuesta.jsonPath().getList("$");
            if (!encontradas.isEmpty()) {
                actor.remember("idMatricula", respuesta.jsonPath().getLong("[0].id"));
            }
        }
    }
}
