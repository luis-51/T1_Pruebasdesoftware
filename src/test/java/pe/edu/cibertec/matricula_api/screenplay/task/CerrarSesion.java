package pe.edu.cibertec.matricula_api.screenplay.task;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

/** POST /api/auth/logout con el accessToken de la memoria del actor. */
public class CerrarSesion implements Task {

    public static CerrarSesion ahora() {
        return new CerrarSesion();
    }

    @Override
    @Step("{0} cierra sesion")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Post.to("/api/auth/logout")
                        .with(req -> req.auth().oauth2(token))
        );
    }
}
