package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record AlumnoRequest(
        @Schema(example = "i202610008") @NotBlank
        @Pattern(regexp = "i\\d{9}", message = "debe tener el formato i + 9 dígitos") String codigo,
        @Schema(example = "Mario") @NotBlank @Size(max = 60) String nombres,
        @Schema(example = "Vega") @NotBlank @Size(max = 60) String apellidos,
        @Schema(example = "mario.vega@cibertec.edu.pe") @NotBlank @Email String email,
        @Schema(example = "17.5") @NotNull @DecimalMin("0.0") @DecimalMax("20.0") Double promedio,
        @Schema(example = "false") Boolean tieneDeuda,
        @Schema(example = "CI") @NotBlank String codigoCarrera) {

    public boolean conDeuda() {
        return Boolean.TRUE.equals(tieneDeuda);
    }
}
