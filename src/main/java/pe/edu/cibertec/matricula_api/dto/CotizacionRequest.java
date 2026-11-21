package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CotizacionRequest(
        @Schema(example = "i202610001") @NotBlank String codigoAlumno,
        @Schema(example = "[\"PS5511\", \"TS5514\"]") @NotEmpty List<@NotBlank String> cursos) {
}
