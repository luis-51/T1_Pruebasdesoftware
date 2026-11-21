package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.*;
import pe.edu.cibertec.matricula_api.service.AuthService;

@Tag(name = "Autenticación")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @Operation(summary = "Iniciar sesión y obtener access token y refresh token")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Sesión iniciada")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "401", description = "CREDENCIALES_INVALIDAS")
    @ApiResponse(responseCode = "423", description = "CUENTA_BLOQUEADA")
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    @Operation(summary = "Renovar tokens con el refresh token (el anterior queda invalidado)")
    @SecurityRequirements
    @ApiResponse(responseCode = "200", description = "Tokens renovados")
    @ApiResponse(responseCode = "401", description = "REFRESH_INVALIDO o REFRESH_REUTILIZADO")
    @ApiResponse(responseCode = "423", description = "CUENTA_BLOQUEADA")
    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest req) {
        return service.refresh(req);
    }

    @Operation(summary = "Cerrar la sesión actual")
    @ApiResponse(responseCode = "204", description = "Sesión cerrada")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        service.logout();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Datos del usuario autenticado")
    @GetMapping("/perfil")
    public PerfilResponse perfil() {
        return service.perfil();
    }

    @Operation(summary = "Cambiar la contraseña (cierra todas las sesiones)")
    @ApiResponse(responseCode = "204", description = "Contraseña cambiada")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "422", description = "PASSWORD_INCORRECTA o PASSWORD_REPETIDA")
    @PutMapping("/password")
    public ResponseEntity<Void> cambiarPassword(@Valid @RequestBody PasswordRequest req) {
        service.cambiarPassword(req);
        return ResponseEntity.noContent().build();
    }
}
