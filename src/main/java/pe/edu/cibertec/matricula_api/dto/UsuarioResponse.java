package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.Rol;
import pe.edu.cibertec.matricula_api.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(String username, Rol rol, boolean bloqueado, int intentosFallidos,
                              LocalDateTime bloqueadoHasta) {

    public static UsuarioResponse of(Usuario u) {
        return new UsuarioResponse(u.getUsername(), u.getRol(), u.bloqueado(), u.getIntentosFallidos(),
                u.bloqueado() ? u.getBloqueadoHasta() : null);
    }
}
