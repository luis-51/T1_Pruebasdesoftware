package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.PeriodoRequest;
import pe.edu.cibertec.matricula_api.dto.PeriodoResponse;
import pe.edu.cibertec.matricula_api.service.PeriodoService;

import java.net.URI;
import java.util.List;

@Tag(name = "Periodos")
@RestController
@RequestMapping("/api/periodos")
public class PeriodoController {

    private final PeriodoService service;

    public PeriodoController(PeriodoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar periodos académicos")
    @GetMapping
    public List<PeriodoResponse> listar() {
        return service.listar();
    }

    @Operation(summary = "Periodo abierto para matrícula")
    @ApiResponse(responseCode = "200", description = "Periodo abierto")
    @ApiResponse(responseCode = "404", description = "SIN_PERIODO_ABIERTO")
    @GetMapping("/actual")
    public PeriodoResponse actual() {
        return service.actual();
    }

    @Operation(summary = "Obtener un periodo")
    @ApiResponse(responseCode = "200", description = "Periodo encontrado")
    @ApiResponse(responseCode = "404", description = "PERIODO_NO_ENCONTRADO")
    @GetMapping("/{codigo}")
    public PeriodoResponse obtener(@PathVariable String codigo) {
        return service.obtener(codigo);
    }

    @Operation(summary = "Registrar un periodo; se crea cerrado (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Periodo registrado")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS o FECHAS_INVALIDAS")
    @ApiResponse(responseCode = "409", description = "PERIODO_DUPLICADO")
    @PostMapping
    public ResponseEntity<PeriodoResponse> crear(@Valid @RequestBody PeriodoRequest req) {
        PeriodoResponse res = service.crear(req);
        return ResponseEntity.created(URI.create("/api/periodos/" + res.codigo())).body(res);
    }

    @Operation(summary = "Abrir un periodo para matrícula (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Periodo abierto")
    @ApiResponse(responseCode = "404", description = "PERIODO_NO_ENCONTRADO")
    @ApiResponse(responseCode = "409", description = "PERIODO_YA_ABIERTO o PERIODO_ABIERTO_EXISTENTE")
    @PatchMapping("/{codigo}/abrir")
    public PeriodoResponse abrir(@PathVariable String codigo) {
        return service.abrir(codigo);
    }

    @Operation(summary = "Cerrar un periodo; anula las matrículas sin pagar (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Periodo cerrado")
    @ApiResponse(responseCode = "404", description = "PERIODO_NO_ENCONTRADO")
    @ApiResponse(responseCode = "409", description = "PERIODO_YA_CERRADO")
    @PatchMapping("/{codigo}/cerrar")
    public PeriodoResponse cerrar(@PathVariable String codigo) {
        return service.cerrar(codigo);
    }
}
