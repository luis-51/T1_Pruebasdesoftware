package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record MatriculaRequest(
        @Schema(example = "i202610001") @NotBlank String codigoAlumno,
        @Schema(example = "PS5511") @NotBlank String codigoCurso) {
}
