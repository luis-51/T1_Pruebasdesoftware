package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record DocenteRequest(
        @Schema(example = "D0006") @NotBlank
        @Pattern(regexp = "D\\d{4}", message = "debe tener el formato D + 4 dígitos") String codigo,
        @Schema(example = "Carmen") @NotBlank @Size(max = 60) String nombres,
        @Schema(example = "Ruiz") @NotBlank @Size(max = 60) String apellidos,
        @Schema(example = "carmen.ruiz@cibertec.edu.pe") @NotBlank @Email String email,
        @Schema(example = "Arquitectura de software") @NotBlank @Size(max = 80) String especialidad) {
}
