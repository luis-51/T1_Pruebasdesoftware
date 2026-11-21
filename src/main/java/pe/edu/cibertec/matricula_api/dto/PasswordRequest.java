package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PasswordRequest(
        @Schema(example = "ClaveActual2026") @NotBlank String actual,
        @Schema(example = "NuevaClave2026") @NotBlank
        @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,64}",
                message = "debe tener de 8 a 64 caracteres, con mayúscula, minúscula y número") String nueva) {
}
