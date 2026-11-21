package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import pe.edu.cibertec.matricula_api.screenplay.config.ApiConfig;
import pe.edu.cibertec.matricula_api.screenplay.questions.LosTokens;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarPerfil;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.ListarAlumnos;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.is;

public class SeguridadStepDefinitions {

    // ---------- Rotación del refresh token ----------

    @When("renueva sus tokens con su refreshToken")
    public void renuevaSusTokens() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.con("refreshToken"));
    }

    @And("recibe un accessToken y un refreshToken nuevos")
    public void recibeTokensNuevos() {
        theActorInTheSpotlight().should(seeThat(LosTokens.sonNuevos(), is(true)));
    }

    @When("intenta renovar otra vez con el refreshToken original")
    public void intentaRenovarConElOriginal() {
        theActorInTheSpotlight().attemptsTo(RenovarToken.con("refreshTokenUsado"));
    }

    @When("consulta su perfil con su accessToken")
    public void consultaSuPerfil() {
        theActorInTheSpotlight().attemptsTo(ConsultarPerfil.propio());
    }

    // ---------- Cierre de sesión ----------

    @When("cierra sesión")
    public void cierraSesion() {
        theActorInTheSpotlight().attemptsTo(CerrarSesion.ahora());
    }

    // ---------- Bloqueo de cuenta ----------

    @When("el alumno {string} intenta ingresar con la clave {string} en el intento {int}")
    public void intentaIngresar(String usuario, String clave, int intento) {
        theActorCalled(usuario).whoCan(CallAnApi.at(ApiConfig.baseUrl()));
        theActorInTheSpotlight().attemptsTo(IniciarSesion.como(usuario, clave));
    }

    // ---------- Alumno sin permiso de administrador ----------

    @When("intenta listar todos los alumnos")
    public void intentaListarTodosLosAlumnos() {
        theActorInTheSpotlight().attemptsTo(ListarAlumnos.todos());
    }
}
