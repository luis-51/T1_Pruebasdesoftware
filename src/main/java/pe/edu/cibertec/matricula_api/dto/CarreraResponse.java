package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.Carrera;

public record CarreraResponse(String codigo, String nombre) {

    public static CarreraResponse of(Carrera c) {
        return new CarreraResponse(c.getCodigo(), c.getNombre());
    }
}
