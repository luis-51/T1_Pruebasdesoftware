package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.Curso;
import pe.edu.cibertec.matricula_api.model.DiaSemana;
import pe.edu.cibertec.matricula_api.model.Docente;
import pe.edu.cibertec.matricula_api.model.Modalidad;

import java.time.LocalTime;
import java.util.List;

public record CursoResponse(
        String codigo,
        String nombre,
        int ciclo,
        int creditos,
        Modalidad modalidad,
        DiaSemana dia,
        LocalTime horaInicio,
        LocalTime horaFin,
        int vacantes,
        int disponibles,
        String carrera,
        String codigoDocente,
        String docente,
        List<String> requisitos) {

    public static CursoResponse of(Curso c, int ocupadas) {
        Docente d = c.getDocente();
        return new CursoResponse(c.getCodigo(), c.getNombre(), c.getCiclo(), c.getCreditos(),
                c.getModalidad(), c.getDia(), c.getHoraInicio(), c.getHoraFin(),
                c.getVacantes(), c.getVacantes() - ocupadas, c.getCarrera().getCodigo(),
                d == null ? null : d.getCodigo(), d == null ? null : d.nombreCompleto(),
                c.requisitosOrdenados().stream().map(Curso::getCodigo).toList());
    }
}
