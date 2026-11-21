package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cibertec.matricula_api.model.*;

import java.util.List;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {

    @Query("""
            select m from Matricula m
            where (:alumno is null or m.alumno.codigo = :alumno)
              and (:curso is null or m.curso.codigo = :curso)
              and (:estado is null or m.estado = :estado)
              and (:periodo is null or m.periodo.codigo = :periodo)
            order by m.id
            """)
    List<Matricula> buscar(@Param("alumno") String alumno,
                           @Param("curso") String curso,
                           @Param("estado") EstadoMatricula estado,
                           @Param("periodo") String periodo);

    List<Matricula> findByAlumnoAndPeriodoAndEstadoNotOrderById(Alumno alumno, Periodo periodo, EstadoMatricula estado);

    List<Matricula> findByCursoAndPeriodoAndEstadoNotOrderById(Curso curso, Periodo periodo, EstadoMatricula estado);

    List<Matricula> findByPeriodoAndEstado(Periodo periodo, EstadoMatricula estado);

    List<Matricula> findByAlumnoAndNotaIsNotNullOrderByPeriodoCodigoAscCursoCodigoAsc(Alumno alumno);

    int countByCursoAndPeriodoAndEstadoNot(Curso curso, Periodo periodo, EstadoMatricula estado);

    boolean existsByAlumnoAndCursoAndNotaGreaterThanEqual(Alumno alumno, Curso curso, Double nota);

    boolean existsByAlumno(Alumno alumno);

    boolean existsByCurso(Curso curso);
}
