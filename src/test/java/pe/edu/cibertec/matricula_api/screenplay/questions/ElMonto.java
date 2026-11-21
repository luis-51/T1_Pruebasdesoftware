package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class ElMonto {

    public static Question<Double> deLamaTricula() {
        return Question.about("el monto de la matricula")
                .answeredBy(
                        actor -> SerenityRest.lastResponse().jsonPath().getDouble("monto")
                );
    }

    public static Question<Double> cotizado() {
        return Question.about("el total cotizado que recuerda el actor")
                .answeredBy(actor -> actor.<Double>recall("totalCotizado"));
    }
}
