package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import pe.edu.cibertec.matricula_api.model.MetodoPago;

public record PagoRequest(@Schema(example = "YAPE") MetodoPago metodo) {

    public MetodoPago metodoOEfectivo() {
        return metodo == null ? MetodoPago.EFECTIVO : metodo;
    }
}
