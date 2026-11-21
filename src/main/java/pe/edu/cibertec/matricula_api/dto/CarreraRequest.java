package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CarreraRequest(
        @Schema(example = "DS") @NotBlank
        @Pattern(regexp = "[A-Z]{2,4}", message = "debe tener de 2 a 4 letras mayúsculas") String codigo,
        @Schema(example = "Diseño de Software") @NotBlank @Size(max = 80) String nombre) {
}
