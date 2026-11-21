package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.*;
import pe.edu.cibertec.matricula_api.model.EstadoMatricula;
import pe.edu.cibertec.matricula_api.service.MatriculaService;

import java.net.URI;
import java.util.List;

@Tag(name = "Matrículas")
@RestController
@RequestMapping("/api/matriculas")
public class MatriculaController {

    private final MatriculaService service;

    public MatriculaController(MatriculaService service) {
        this.service = service;
    }

    @Operation(summary = "Cotizar cursos antes de matricular (no guarda nada)")
    @ApiResponse(responseCode = "200", description = "Cotización calculada")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "ALUMNO_NO_ENCONTRADO o CURSO_NO_ENCONTRADO")
    @ApiResponse(responseCode = "422", description = "LIMITE_CREDITOS")
    @PostMapping("/cotizacion")
    public CotizacionResponse cotizar(@Valid @RequestBody CotizacionRequest req) {
        return service.cotizar(req);
    }

    @Operation(summary = "Matricular a un alumno en un curso del periodo abierto")
    @ApiResponse(responseCode = "201", description = "Matrícula registrada")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "ALUMNO_NO_ENCONTRADO o CURSO_NO_ENCONTRADO")
    @ApiResponse(responseCode = "409", description = "MATRICULA_DUPLICADA, SIN_VACANTES o CRUCE_HORARIO")
    @ApiResponse(responseCode = "422", description = "SIN_PERIODO_ABIERTO, CURSO_DE_OTRA_CARRERA, ALUMNO_CON_DEUDA, "
            + "CURSO_APROBADO, PRERREQUISITO_PENDIENTE o LIMITE_CREDITOS")
    @PostMapping
    public ResponseEntity<MatriculaResponse> registrar(@Valid @RequestBody MatriculaRequest req) {
        MatriculaResponse res = service.registrar(req);
        return ResponseEntity.created(URI.create("/api/matriculas/" + res.id())).body(res);
    }

    @Operation(summary = "Listar matrículas (ADMIN: todas; ALUMNO: solo las suyas)")
    @GetMapping
    public List<MatriculaResponse> listar(@RequestParam(required = false) String alumno,
                                          @RequestParam(required = false) String curso,
                                          @RequestParam(required = false) EstadoMatricula estado,
                                          @RequestParam(required = false) String periodo) {
        return service.listar(alumno, curso, estado, periodo);
    }

    @Operation(summary = "Obtener una matrícula (ADMIN, el alumno o el docente del curso)")
    @ApiResponse(responseCode = "200", description = "Matrícula encontrada")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "MATRICULA_NO_ENCONTRADA")
    @GetMapping("/{id}")
    public MatriculaResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }

    @Operation(summary = "Pagar una matrícula; el método es opcional y por defecto EFECTIVO")
    @ApiResponse(responseCode = "200", description = "Matrícula pagada")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "MATRICULA_NO_ENCONTRADA")
    @ApiResponse(responseCode = "409", description = "MATRICULA_YA_PAGADA o MATRICULA_ANULADA")
    @PatchMapping("/{id}/pagar")
    public MatriculaResponse pagar(@PathVariable Long id, @RequestBody(required = false) PagoRequest req) {
        return service.pagar(id, req);
    }

    @Operation(summary = "Anular una matrícula no pagada y liberar la vacante")
    @ApiResponse(responseCode = "200", description = "Matrícula anulada")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "MATRICULA_NO_ENCONTRADA")
    @ApiResponse(responseCode = "409", description = "MATRICULA_YA_ANULADA o MATRICULA_PAGADA")
    @PatchMapping("/{id}/anular")
    public MatriculaResponse anular(@PathVariable Long id) {
        return service.anular(id);
    }

    @Operation(summary = "Registrar la nota final (ADMIN o docente del curso)")
    @ApiResponse(responseCode = "200", description = "Nota registrada")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "MATRICULA_NO_ENCONTRADA")
    @ApiResponse(responseCode = "409", description = "MATRICULA_NO_PAGADA")
    @PatchMapping("/{id}/nota")
    public MatriculaResponse calificar(@PathVariable Long id, @Valid @RequestBody NotaRequest req) {
        return service.calificar(id, req);
    }
}
