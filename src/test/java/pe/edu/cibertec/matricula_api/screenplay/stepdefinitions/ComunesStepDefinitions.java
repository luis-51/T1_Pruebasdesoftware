package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import pe.edu.cibertec.matricula_api.screenplay.config.ApiConfig;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElCodigoDeError;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;

public class ComunesStepDefinitions {

    @Given("que {word} consulta la API de matricula")
    public void consultaLaAPI(String nombre) {
        theActorCalled(nombre).whoCan(CallAnApi.at(ApiConfig.baseUrl()));
    }

    @Given("que el alumno {string} inicia sesión con la clave {string}")
    public void elAlumnoIniciaSesion(String usuario, String clave) {
        iniciarSesion(usuario, clave);
    }

    @Given("que la alumna {string} inicia sesión con la clave {string}")
    public void laAlumnaIniciaSesion(String usuario, String clave) {
        iniciarSesion(usuario, clave);
    }

    @Given("que el docente {string} inicia sesión con la clave {string}")
    public void elDocenteIniciaSesion(String usuario, String clave) {
        iniciarSesion(usuario, clave);
    }

    @Then("el codigo de respuesta es {int}")
    public void elCodigoDeRespuestaEsInt(int status) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(status)));
    }

    @And("el codigo de error es {string}")
    public void elCodigoDeErrorEsString(String codigo) {
        theActorInTheSpotlight().should(seeThat(ElCodigoDeError.enLaRespuesta(), equalTo(codigo)));
    }

    private void iniciarSesion(String usuario, String clave) {
        theActorCalled(usuario).whoCan(CallAnApi.at(ApiConfig.baseUrl()));
        theActorInTheSpotlight().attemptsTo(IniciarSesion.como(usuario, clave));
        theActorInTheSpotlight().should(seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)));
    }
}
