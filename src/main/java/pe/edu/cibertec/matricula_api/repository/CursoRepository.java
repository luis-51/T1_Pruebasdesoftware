package pe.edu.cibertec.matricula_api.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cibertec.matricula_api.model.*;

import java.util.List;
import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    Optional<Curso> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Curso c where c.codigo = :codigo")
    Optional<Curso> bloquear(@Param("codigo") String codigo);

    boolean existsByCarrera(Carrera carrera);

    boolean existsByDocente(Docente docente);

    List<Curso> findByDocenteOrderByCodigo(Docente docente);

    @Query("select count(c) > 0 from Curso c join c.requisitos r where r = :curso")
    boolean esRequisito(@Param("curso") Curso curso);

    @Query("""
            select c from Curso c
            where (:ciclo is null or c.ciclo = :ciclo)
              and (:modalidad is null or c.modalidad = :modalidad)
              and (:dia is null or c.dia = :dia)
              and (:carrera is null or c.carrera.codigo = :carrera)
              and (:nombre is null or lower(c.nombre) like lower(concat('%', :nombre, '%')))
            order by c.codigo
            """)
    List<Curso> buscar(@Param("ciclo") Integer ciclo,
                       @Param("modalidad") Modalidad modalidad,
                       @Param("dia") DiaSemana dia,
                       @Param("carrera") String carrera,
                       @Param("nombre") String nombre);
}
