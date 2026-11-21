package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

/** GET /api/alumnos/{codigo}/historial del propio alumno. */
public class ConsultarHistorial implements Task {

    public static ConsultarHistorial propio() {
        return new ConsultarHistorial();
    }

    @Override
    @Step("{0} consulta su historial academico")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        String alumno = actor.recall("username");
        actor.attemptsTo(
                Get.resource("/api/alumnos/{codigo}/historial")
                        .with(req -> req.pathParam("codigo", alumno).auth().oauth2(token))
        );
    }
}
