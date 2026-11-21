package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

/** GET /api/alumnos (solo ADMIN) con el accessToken de la memoria del actor. */
public class ListarAlumnos implements Task {

    public static ListarAlumnos todos() {
        return new ListarAlumnos();
    }

    @Override
    @Step("{0} lista todos los alumnos")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/alumnos")
                        .with(req -> req.auth().oauth2(token))
        );
    }
}
