package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.EstadoPeriodo;
import pe.edu.cibertec.matricula_api.model.Periodo;

import java.time.LocalDate;

public record PeriodoResponse(String codigo, LocalDate fechaInicio, LocalDate fechaFin, EstadoPeriodo estado) {

    public static PeriodoResponse of(Periodo p) {
        return new PeriodoResponse(p.getCodigo(), p.getFechaInicio(), p.getFechaFin(), p.getEstado());
    }
}
