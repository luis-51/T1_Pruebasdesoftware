package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

/** Preguntas sobre la respuesta de GET /api/pagos (lista de pagos). */
public class ElPago {

    public static Question<Integer> cantidadDeLaMatricula(long idMatricula) {
        return Question.about("la cantidad de pagos de la matricula " + idMatricula)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getList("findAll { it.idMatricula == " + idMatricula + " }").size());
    }

    public static Question<String> metodoDeLaMatricula(long idMatricula) {
        return Question.about("el metodo del pago de la matricula " + idMatricula)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getString("find { it.idMatricula == " + idMatricula + " }.metodo"));
    }

    public static Question<String> numeroOperacionDeLaMatricula(long idMatricula) {
        return Question.about("el numero de operacion del pago de la matricula " + idMatricula)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getString("find { it.idMatricula == " + idMatricula + " }.numeroOperacion"));
    }
}
