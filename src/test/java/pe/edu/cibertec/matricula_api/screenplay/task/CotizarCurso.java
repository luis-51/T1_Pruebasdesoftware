package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.List;
import java.util.Map;

/** POST /api/matriculas/cotizacion. Guarda el total cotizado si responde 200. */
public class CotizarCurso implements Task {

    private final String curso;

    public CotizarCurso(String curso) {
        this.curso = curso;
    }

    public static CotizarCurso de(String curso) {
        return new CotizarCurso(curso);
    }

    @Override
    @Step("{0} cotiza el curso #curso")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        String alumno = actor.recall("username");
        actor.attemptsTo(
                Post.to("/api/matriculas/cotizacion")
                        .with(req -> req.auth().oauth2(token)
                                .contentType(ContentType.JSON)
                                .body(Map.of("codigoAlumno", alumno, "cursos", List.of(curso))))
        );
        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 200) {
            actor.remember("totalCotizado", respuesta.jsonPath().getDouble("total"));
        }
    }
}
