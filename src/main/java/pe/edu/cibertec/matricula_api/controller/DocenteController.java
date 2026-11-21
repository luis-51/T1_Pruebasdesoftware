package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.CursoResponse;
import pe.edu.cibertec.matricula_api.dto.DocenteRequest;
import pe.edu.cibertec.matricula_api.dto.DocenteResponse;
import pe.edu.cibertec.matricula_api.service.CursoService;
import pe.edu.cibertec.matricula_api.service.DocenteService;

import java.net.URI;
import java.util.List;

@Tag(name = "Docentes")
@RestController
@RequestMapping("/api/docentes")
public class DocenteController {

    private final DocenteService service;
    private final CursoService cursoService;

    public DocenteController(DocenteService service, CursoService cursoService) {
        this.service = service;
        this.cursoService = cursoService;
    }

    @Operation(summary = "Listar docentes, opcionalmente por nombre (ADMIN)")
    @GetMapping
    public List<DocenteResponse> listar(@RequestParam(required = false) String nombre) {
        return service.listar(nombre);
    }

    @Operation(summary = "Obtener un docente (ADMIN o el mismo docente)")
    @ApiResponse(responseCode = "200", description = "Docente encontrado")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "DOCENTE_NO_ENCONTRADO")
    @GetMapping("/{codigo}")
    public DocenteResponse obtener(@PathVariable String codigo) {
        return service.obtener(codigo);
    }

    @Operation(summary = "Cursos asignados a un docente (ADMIN o el mismo docente)")
    @ApiResponse(responseCode = "200", description = "Cursos del docente")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "DOCENTE_NO_ENCONTRADO")
    @GetMapping("/{codigo}/cursos")
    public List<CursoResponse> cursos(@PathVariable String codigo) {
        return cursoService.deDocente(codigo);
    }

    @Operation(summary = "Registrar un docente y su cuenta; devuelve una clave temporal (ADMIN)")
    @ApiResponse(responseCode = "201", description = "Docente registrado")
    @ApiResponse(responseCode = "400", description = "DATOS_INVALIDOS")
    @ApiResponse(responseCode = "409", description = "DOCENTE_DUPLICADO o EMAIL_DUPLICADO")
    @PostMapping
    public ResponseEntity<DocenteResponse> crear(@Valid @RequestBody DocenteRequest req) {
        DocenteResponse res = service.crear(req);
        return ResponseEntity.created(URI.create("/api/docentes/" + res.codigo())).body(res);
    }

    @Operation(summary = "Actualizar un docente (ADMIN)")
    @ApiResponse(responseCode = "200", description = "Docente actualizado")
    @ApiResponse(responseCode = "404", description = "DOCENTE_NO_ENCONTRADO")
    @ApiResponse(responseCode = "409", description = "EMAIL_DUPLICADO")
    @PutMapping("/{codigo}")
    public DocenteResponse actualizar(@PathVariable String codigo, @Valid @RequestBody DocenteRequest req) {
        return service.actualizar(codigo, req);
    }

    @Operation(summary = "Eliminar un docente sin cursos y su cuenta (ADMIN)")
    @ApiResponse(responseCode = "204", description = "Docente eliminado")
    @ApiResponse(responseCode = "404", description = "DOCENTE_NO_ENCONTRADO")
    @ApiResponse(responseCode = "409", description = "DOCENTE_CON_CURSOS")
    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> eliminar(@PathVariable String codigo) {
        service.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }
}
