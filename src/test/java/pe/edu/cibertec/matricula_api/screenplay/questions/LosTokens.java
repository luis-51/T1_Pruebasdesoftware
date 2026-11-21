package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.screenplay.Question;

/** Compara los tokens guardados en la memoria del actor despues de renovar. */
public class LosTokens {

    public static Question<Boolean> sonNuevos() {
        return Question.about("los tokens son nuevos y distintos de los anteriores")
                .answeredBy(actor -> {
                    String access = actor.recall("accessToken");
                    String refresh = actor.recall("refreshToken");
                    String accessAnterior = actor.recall("accessTokenAnterior");
                    String refreshUsado = actor.recall("refreshTokenUsado");
                    return access != null && !access.isBlank() && !access.equals(accessAnterior)
                            && refresh != null && !refresh.isBlank() && !refresh.equals(refreshUsado);
                });
    }
}
