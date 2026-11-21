package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.UsuarioResponse;
import pe.edu.cibertec.matricula_api.service.UsuarioService;

import java.util.List;

@Tag(name = "Usuarios")
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @Operation(summary = "Listar usuarios y su estado de bloqueo (ADMIN)")
    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Desbloquear una cuenta (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Cuenta desbloqueada")
    @ApiResponse(responseCode = "404", description = "USUARIO_NO_ENCONTRADO")
    @PatchMapping("/{username}/desbloquear")
    public UsuarioResponse desbloquear(@PathVariable String username) {
        return service.desbloquear(username);
    }
}
