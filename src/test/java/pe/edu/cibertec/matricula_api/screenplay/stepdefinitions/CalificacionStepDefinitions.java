package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarMatriculaDelCurso;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;

public class CalificacionStepDefinitions {

    private static final double TOLERANCIA = 0.001;

    @When("la alumna {string} obtiene el id de su matrícula del curso {string}")
    public void obtieneElIdDeSuMatricula(String alumna, String curso) {
        theActorCalled(alumna).attemptsTo(ConsultarMatriculaDelCurso.de(curso));
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)),
                seeThat(LaRespuesta.campo("[0].codigoCurso"), equalTo(curso)));
    }

    @When("el docente {string} registra la nota {double} de la matrícula de la alumna {string}")
    public void elDocenteRegistraLaNota(String docente, double nota, String alumna) {
        long idMatricula = theActorCalled(alumna).recall("idMatricula");
        theActorCalled(docente).attemptsTo(CalificarMatricula.conId(idMatricula, nota));
    }

    @And("la condición de la matrícula es {string}")
    public void laCondicionDeLaMatriculaEs(String condicion) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.campo("condicion"), equalTo(condicion)));
    }

    @When("la alumna {string} consulta su historial")
    public void laAlumnaConsultaSuHistorial(String alumna) {
        theActorCalled(alumna).attemptsTo(ConsultarHistorial.propio());
    }

    @And("el historial indica {int} créditos aprobados")
    public void elHistorialIndicaCreditosAprobados(int creditos) {
        theActorInTheSpotlight().should(seeThat(ElHistorial.creditosAprobados(), equalTo(creditos)));
    }

    @And("el promedio ponderado del historial es {double}")
    public void elPromedioPonderadoEs(double promedio) {
        theActorInTheSpotlight().should(
                seeThat(ElHistorial.promedioPonderado(), closeTo(promedio, TOLERANCIA)));
    }

    @And("el curso {string} figura en el historial con nota {double} y condición {string}")
    public void elCursoFiguraEnElHistorial(String curso, double nota, String condicion) {
        theActorInTheSpotlight().should(
                seeThat(ElHistorial.notaDelCurso(curso), closeTo(nota, TOLERANCIA)),
                seeThat(ElHistorial.condicionDelCurso(curso), equalTo(condicion)));
    }
}
