package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.EstadoMatricula;
import pe.edu.cibertec.matricula_api.model.Matricula;

import java.time.LocalDateTime;

public record MatriculaResponse(
        Long id,
        String codigoAlumno,
        String codigoCurso,
        String periodo,
        String horario,
        int creditos,
        double monto,
        EstadoMatricula estado,
        Double nota,
        String condicion,
        LocalDateTime fechaRegistro,
        LocalDateTime fechaPago) {

    public static MatriculaResponse of(Matricula m) {
        return new MatriculaResponse(m.getId(), m.getAlumno().getCodigo(), m.getCurso().getCodigo(),
                m.getPeriodo().getCodigo(), m.getCurso().horario(), m.getCurso().getCreditos(), m.getMonto(),
                m.getEstado(), m.getNota(), m.condicion(), m.getFechaRegistro(), m.getFechaPago());
    }
}
