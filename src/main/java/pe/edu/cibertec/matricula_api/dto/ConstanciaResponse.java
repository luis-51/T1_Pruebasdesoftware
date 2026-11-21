package pe.edu.cibertec.matricula_api.dto;

import java.util.List;

public record ConstanciaResponse(
        String codigoAlumno,
        String alumno,
        String periodo,
        double promedio,
        int descuento,
        List<MatriculaResponse> matriculas,
        int creditos,
        double montoTotal,
        double montoPagado,
        double montoPendiente) {
}
