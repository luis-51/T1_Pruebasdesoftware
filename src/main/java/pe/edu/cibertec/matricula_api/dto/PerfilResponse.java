package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.Rol;

public record PerfilResponse(String username, Rol rol, String nombre) {
}
