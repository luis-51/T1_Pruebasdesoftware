package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record PeriodoRequest(
        @Schema(example = "2027-1") @NotBlank
        @Pattern(regexp = "\\d{4}-[12]", message = "debe tener el formato AAAA-1 o AAAA-2") String codigo,
        @Schema(type = "string", example = "2027-03-15") @NotNull LocalDate fechaInicio,
        @Schema(type = "string", example = "2027-07-10") @NotNull LocalDate fechaFin) {
}
