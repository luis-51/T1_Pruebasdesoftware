package pe.edu.cibertec.matricula_api.dto;

import java.util.List;

public record HistorialResponse(
        String codigoAlumno,
        String alumno,
        String carrera,
        List<Item> cursos,
        int creditosAprobados,
        double promedioPonderado) {

    public record Item(String periodo, String codigoCurso, String curso, int creditos, double nota, String condicion) {
    }
}
