package pe.edu.cibertec.matricula_api.dto;

import pe.edu.cibertec.matricula_api.model.Rol;

public record TokenResponse(
        String accessToken,
        String refreshToken,
        String tipo,
        long expiraEn,
        String username,
        Rol rol) {
}
