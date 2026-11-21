package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.Matricula;
import pe.edu.cibertec.matricula_api.model.MetodoPago;
import pe.edu.cibertec.matricula_api.model.Pago;

import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        String numeroOperacion,
        Long idMatricula,
        String codigoAlumno,
        String codigoCurso,
        String periodo,
        MetodoPago metodo,
        double monto,
        LocalDateTime fecha) {

    public static PagoResponse of(Pago p) {
        Matricula m = p.getMatricula();
        return new PagoResponse(p.getId(), p.numeroOperacion(), m.getId(), m.getAlumno().getCodigo(),
                m.getCurso().getCodigo(), m.getPeriodo().getCodigo(), p.getMetodo(), p.getMonto(), p.getFecha());
    }
}
