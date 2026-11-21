package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.EstadoMatricula;
import pe.edu.cibertec.matricula_api.model.Matricula;

public record AlumnoMatriculadoResponse(
        Long idMatricula,
        String codigoAlumno,
        String alumno,
        EstadoMatricula estado,
        Double nota,
        String condicion) {

    public static AlumnoMatriculadoResponse of(Matricula m) {
        return new AlumnoMatriculadoResponse(m.getId(), m.getAlumno().getCodigo(),
                m.getAlumno().nombreCompleto(), m.getEstado(), m.getNota(), m.condicion());
    }
}
