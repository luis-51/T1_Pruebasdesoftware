package pe.edu.cibertec.matricula_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import pe.edu.cibertec.matricula_api.model.Docente;

public record DocenteResponse(
        String codigo,
        String nombres,
        String apellidos,
        String email,
        String especialidad,
        @JsonInclude(JsonInclude.Include.NON_NULL) String claveTemporal) {

    public static DocenteResponse of(Docente d) {
        return of(d, null);
    }

    public static DocenteResponse of(Docente d, String claveTemporal) {
        return new DocenteResponse(d.getCodigo(), d.getNombres(), d.getApellidos(), d.getEmail(),
                d.getEspecialidad(), claveTemporal);
    }
}
