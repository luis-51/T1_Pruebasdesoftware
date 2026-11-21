package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cibertec.matricula_api.model.Alumno;
import pe.edu.cibertec.matricula_api.model.Carrera;

import java.util.List;
import java.util.Optional;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    Optional<Alumno> findByCodigo(String codigo);

    Optional<Alumno> findByEmail(String email);

    boolean existsByCodigo(String codigo);

    boolean existsByCarrera(Carrera carrera);

    @Query("""
            select a from Alumno a
            where (:nombre is null or lower(concat(a.nombres, ' ', a.apellidos)) like lower(concat('%', :nombre, '%')))
              and (:deuda is null or a.tieneDeuda = :deuda)
              and (:carrera is null or a.carrera.codigo = :carrera)
            order by a.codigo
            """)
    List<Alumno> buscar(@Param("nombre") String nombre,
                        @Param("deuda") Boolean deuda,
                        @Param("carrera") String carrera);
}
