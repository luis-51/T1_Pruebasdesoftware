package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElMonto;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElPago;
import pe.edu.cibertec.matricula_api.screenplay.questions.LaRespuesta;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarMatriculaDelCurso;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarPagos;
import pe.edu.cibertec.matricula_api.screenplay.task.CotizarCurso;
import pe.edu.cibertec.matricula_api.screenplay.task.PagarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.RegistrarMatricula;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.Matchers.notNullValue;

public class MatriculaStepDefinitions {

    private static final double TOLERANCIA = 0.001;

    // ---------- Escenario 1: cotizar ----------

    @When("cotiza el curso {string}")
    public void cotizaElCurso(String curso) {
        theActorInTheSpotlight().attemptsTo(CotizarCurso.de(curso));
    }

    @And("los créditos cotizados son {int}")
    public void losCreditosCotizadosSon(int creditos) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.campoEntero("creditos"), equalTo(creditos)));
    }

    @And("el subtotal cotizado es {double}")
    public void elSubtotalCotizadoEs(double subtotal) {
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.campoNumerico("subtotal"), closeTo(subtotal, TOLERANCIA)));
    }

    @And("el descuento cotizado es {int}")
    public void elDescuentoCotizadoEs(int descuento) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.campoEntero("descuento"), equalTo(descuento)));
    }

    @And("el total cotizado es {double}")
    public void elTotalCotizadoEs(double total) {
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.campoNumerico("total"), closeTo(total, TOLERANCIA)));
    }

    // ---------- Escenario 2: matricularse y pagar ----------

    @When("se matricula en el curso {string}")
    public void seMatriculaEnElCurso(String curso) {
        theActorInTheSpotlight().attemptsTo(RegistrarMatricula.en(curso));
    }

    @And("el estado de la matrícula es {string}")
    public void elEstadoDeLaMatriculaEs(String estado) {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.campo("estado"), equalTo(estado)));
    }

    @And("el monto de la matrícula es {double}")
    public void elMontoDeLaMatriculaEs(double monto) {
        theActorInTheSpotlight().should(seeThat(ElMonto.deLamaTricula(), closeTo(monto, TOLERANCIA)));
    }

    @And("el monto de la matrícula es igual al total cotizado")
    public void elMontoEsIgualAlTotalCotizado() {
        double cotizado = theActorInTheSpotlight().recall("totalCotizado");
        theActorInTheSpotlight().should(seeThat(ElMonto.deLamaTricula(), closeTo(cotizado, TOLERANCIA)));
    }

    @When("paga la matrícula sin cuerpo en la petición")
    public void pagaLaMatriculaSinCuerpo() {
        theActorInTheSpotlight().attemptsTo(PagarMatricula.sinCuerpo());
    }

    @When("paga otra vez la misma matrícula sin cuerpo en la petición")
    public void pagaOtraVezLaMismaMatricula() {
        theActorInTheSpotlight().attemptsTo(PagarMatricula.sinCuerpo());
    }

    @And("la fecha de pago tiene valor")
    public void laFechaDePagoTieneValor() {
        theActorInTheSpotlight().should(seeThat(LaRespuesta.campo("fechaPago"), notNullValue()));
    }

    @When("consulta sus pagos")
    public void consultaSusPagos() {
        theActorInTheSpotlight().attemptsTo(ConsultarPagos.propios());
    }

    @And("existe un solo pago de su matrícula")
    public void existeUnSoloPagoDeSuMatricula() {
        long id = theActorInTheSpotlight().recall("idMatricula");
        theActorInTheSpotlight().should(seeThat(ElPago.cantidadDeLaMatricula(id), equalTo(1)));
    }

    @And("el pago de su matrícula tiene el método {string}")
    public void elPagoTieneElMetodo(String metodo) {
        long id = theActorInTheSpotlight().recall("idMatricula");
        theActorInTheSpotlight().should(seeThat(ElPago.metodoDeLaMatricula(id), equalTo(metodo)));
    }

    @And("el pago de su matrícula tiene un numero de operacion con formato OP-000000")
    public void elPagoTieneNumeroDeOperacion() {
        long id = theActorInTheSpotlight().recall("idMatricula");
        theActorInTheSpotlight().should(
                seeThat(ElPago.numeroOperacionDeLaMatricula(id), matchesPattern("^OP-[0-9]{6}$")));
    }

    // ---------- Escenario 3: regla de negocio que debe fallar ----------

    @Given("que su matrícula del curso {string} ya está pagada")
    public void suMatriculaYaEstaPagada(String curso) {
        theActorInTheSpotlight().attemptsTo(ConsultarMatriculaDelCurso.de(curso));
        theActorInTheSpotlight().should(
                seeThat(LaRespuesta.codigoDeEstado(), equalTo(200)),
                seeThat(LaRespuesta.campo("[0].estado"), equalTo("PAGADA")));
    }
}
