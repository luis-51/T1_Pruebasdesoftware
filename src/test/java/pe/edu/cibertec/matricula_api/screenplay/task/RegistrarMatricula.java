package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

/** POST /api/matriculas. Si responde 201 guarda el id de la matricula (tomado de la respuesta). */
public class RegistrarMatricula implements Task {

    private final String curso;

    public RegistrarMatricula(String curso) {
        this.curso = curso;
    }

    public static RegistrarMatricula en(String curso) {
        return new RegistrarMatricula(curso);
    }

    @Override
    @Step("{0} se matricula en el curso #curso")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        String alumno = actor.recall("username");
        actor.attemptsTo(
                Post.to("/api/matriculas")
                        .with(req -> req.auth().oauth2(token)
                                .contentType(ContentType.JSON)
                                .body(Map.of("codigoAlumno", alumno, "codigoCurso", curso)))
        );
        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 201) {
            actor.remember("idMatricula", respuesta.jsonPath().getLong("id"));
        }
    }
}
