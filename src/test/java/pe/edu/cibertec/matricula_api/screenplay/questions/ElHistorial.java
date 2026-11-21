package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

/** Preguntas sobre la respuesta de GET /api/alumnos/{codigo}/historial. */
public class ElHistorial {

    public static Question<Integer> creditosAprobados() {
        return Question.about("los creditos aprobados del historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getInt("creditosAprobados"));
    }

    public static Question<Double> promedioPonderado() {
        return Question.about("el promedio ponderado del historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getDouble("promedioPonderado"));
    }

    public static Question<Double> notaDelCurso(String codigoCurso) {
        return Question.about("la nota de " + codigoCurso + " en el historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getDouble("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.nota"));
    }

    public static Question<String> condicionDelCurso(String codigoCurso) {
        return Question.about("la condicion de " + codigoCurso + " en el historial")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath()
                        .getString("cursos.find { it.codigoCurso == '" + codigoCurso + "' }.condicion"));
    }
}
