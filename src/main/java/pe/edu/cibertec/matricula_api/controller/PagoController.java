package pe.edu.cibertec.matricula_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.matricula_api.dto.PagoResponse;
import pe.edu.cibertec.matricula_api.model.MetodoPago;
import pe.edu.cibertec.matricula_api.service.PagoService;

import java.util.List;

@Tag(name = "Pagos")
@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @Operation(summary = "Listar pagos (ADMIN: todos; ALUMNO: solo los suyos)")
    @GetMapping
    public List<PagoResponse> listar(@RequestParam(required = false) String alumno,
                                     @RequestParam(required = false) MetodoPago metodo) {
        return service.listar(alumno, metodo);
    }

    @Operation(summary = "Obtener un pago")
    @ApiResponse(responseCode = "200", description = "Pago encontrado")
    @ApiResponse(responseCode = "403", description = "ACCESO_DENEGADO")
    @ApiResponse(responseCode = "404", description = "PAGO_NO_ENCONTRADO")
    @GetMapping("/{id}")
    public PagoResponse obtener(@PathVariable Long id) {
        return service.obtener(id);
    }
}
