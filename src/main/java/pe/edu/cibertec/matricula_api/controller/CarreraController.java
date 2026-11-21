package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.CarreraRequest;
import pe.edu.cibertec.matricula_api.dto.CarreraResponse;
import pe.edu.cibertec.matricula_api.service.CarreraService;

import java.net.URI;
import java.util.List;

@Tag(name = "Carreras")
@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraService service;

    public CarreraController(CarreraService service) {
        this.service = service;
    }

    @Operation(summary = "Listar carreras")
    @GetMapping
    public List<CarreraResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Obtener una carrera")
    @ApiResponse(responseCode = "200", description = "Carrera encontrada")
    @ApiResponse(responseCode = "404", description = "CARRERA_NO_ENCONTRADA")
    @GetMapping("/{codigo}")
    public CarreraResponse obtener(@PathVariable String codigo) {
        return service.obtener(codigo);
    }

    @Operation(summary = "Registrar una carrera (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Carrera registrada")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "409", description = "CARRERA_DUPLICADA")
    @PostMapping
    public ResponseEntity<CarreraResponse> crear(@Valid @RequestBody CarreraRequest req) {
        CarreraResponse res = service.crear(req);
        return ResponseEntity.created(URI.create("/api/carreras/" + res.codigo())).body(res);
    }

    @Operation(summary = "Cambiar el nombre de una carrera (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Carrera actualizada")
    @ApiResponse(responseCode = "404", description = "CARRERA_NO_ENCONTRADA")
    @PutMapping("/{codigo}")
    public CarreraResponse actualizar(@PathVariable String codigo, @Valid @RequestBody CarreraRequest req) {
        return service.actualizar(codigo, req);
    }

    @Operation(summary = "Eliminar una carrera sin cursos ni alumnos (ADMIN)")
    @ApiResponse(responseCode = "204", description = "Carrera eliminada")
    @ApiResponse(responseCode = "404", description = "CARRERA_NO_ENCONTRADA")
    @ApiResponse(responseCode = "409", description = "CARRERA_EN_USO")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable String codigo) {
        service.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }
}
