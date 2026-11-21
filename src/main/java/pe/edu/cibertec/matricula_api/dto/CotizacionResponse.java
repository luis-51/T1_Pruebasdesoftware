package pe.edu.cibertec.matricula_api.dto;

import java.util.List;

public record CotizacionResponse(
        String codigoAlumno,
        List<String> cursos,
        int creditos,
        double subtotal,
        int descuento,
        double total) {
}
