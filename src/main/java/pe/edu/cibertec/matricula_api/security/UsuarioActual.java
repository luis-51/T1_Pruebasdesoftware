package pe.edu.cibertec.matricula_api.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.Rol;

@Component
public class UsuarioActual {

    public String username() {
        return jwt().getSubject();
    }

    public Rol rol() {
        return Rol.valueOf(jwt().getClaimAsString("rol"));
    }

    public String sesion() {
        return jwt().getClaimAsString("sid");
    }

    public boolean esAdmin() {
        return rol() == Rol.ADMIN;
    }

    public boolean es(Rol rol, String codigo) {
        return rol() == rol && username().equals(codigo);
    }

    public void permitirAlumno(String codigo) {
        if (!esAdmin() && !es(Rol.ALUMNO, codigo)) throw ApiException.prohibido();
    }

    public void permitirDocente(String codigo) {
        if (!esAdmin() && !es(Rol.DOCENTE, codigo)) throw ApiException.prohibido();
    }

    private Jwt jwt() {
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
