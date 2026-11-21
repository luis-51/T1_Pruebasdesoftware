package pe.edu.cibertec.matricula_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import pe.edu.cibertec.matricula_api.model.DiaSemana;
import pe.edu.cibertec.matricula_api.model.Modalidad;

import java.time.LocalTime;
import java.util.List;

public record CursoRequest(
        @Schema(example = "QA5601") @NotBlank
        @Pattern(regexp = "[A-Z]{2}\\d{4}", message = "debe tener 2 letras mayúsculas y 4 dígitos") String codigo,
        @Schema(example = "Calidad de Software") @NotBlank @Size(max = 80) String nombre,
        @Schema(example = "6") @NotNull @Min(1) @Max(6) Integer ciclo,
        @Schema(example = "3") @NotNull @Min(1) @Max(6) Integer creditos,
        @Schema(example = "30") @NotNull @Min(1) @Max(60) Integer vacantes,
        @Schema(example = "VIRTUAL") @NotNull Modalidad modalidad,
        @Schema(example = "SABADO") @NotNull DiaSemana dia,
        @Schema(type = "string", example = "08:00") @NotNull LocalTime horaInicio,
        @Schema(type = "string", example = "11:00") @NotNull LocalTime horaFin,
        @Schema(example = "CI") @NotBlank String codigoCarrera,
        @Schema(example = "D0001") String codigoDocente,
        @Schema(example = "[\"AS5402\"]") List<@NotBlank String> requisitos) {

    public List<String> requisitosOVacio() {
        return requisitos == null ? List.of() : requisitos;
    }
}
