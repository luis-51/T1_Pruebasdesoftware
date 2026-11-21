package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(example = "i202610001") @NotBlank String username,
        @Schema(example = "TuClave2026") @NotBlank String password) {
}
