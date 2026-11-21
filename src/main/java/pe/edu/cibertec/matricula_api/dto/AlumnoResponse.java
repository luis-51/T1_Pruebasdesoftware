package pe.edu.cibertec.matricula_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import pe.edu.cibertec.matricula_api.model.Alumno;

public record AlumnoResponse(
        String codigo,
        String nombres,
        String apellidos,
        String email,
        double promedio,
        boolean tieneDeuda,
        String carrera,
        @JsonInclude(JsonInclude.Include.NON_NULL) String claveTemporal) {

    public static AlumnoResponse of(Alumno a) {
        return of(a, null);
    }

    public static AlumnoResponse of(Alumno a, String claveTemporal) {
        return new AlumnoResponse(a.getCodigo(), a.getNombres(), a.getApellidos(), a.getEmail(),
                a.getPromedio(), a.isTieneDeuda(), a.getCarrera().getCodigo(), claveTemporal);
    }
}
