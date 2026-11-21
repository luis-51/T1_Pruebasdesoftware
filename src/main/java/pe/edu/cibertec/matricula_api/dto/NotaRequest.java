package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record NotaRequest(@Schema(example = "16") @NotNull @DecimalMin("0.0") @DecimalMax("20.0") Double nota) {
}
