package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Question;

public class LaRespuesta {

    public static Question<Integer> codigoDeEstado(){
        return  Question.about("el codigo de estado")
                .answeredBy( actor -> SerenityRest.lastResponse().statusCode());
    }

    public static Question<String> campo(String ruta){
        return Question.about("el campo "+ ruta)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().get(ruta));
    }

    public static Question<Double> campoNumerico(String ruta){
        return Question.about("el campo numerico " + ruta)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getDouble(ruta));
    }

    public static Question<Integer> campoEntero(String ruta){
        return Question.about("el campo entero " + ruta)
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getInt(ruta));
    }

    public static Question<Integer> cantidadDeElementos(){
        return Question.about("la cantidad de elementos")
                .answeredBy(actor -> SerenityRest.lastResponse().jsonPath().getList("$").size());
    }

}
