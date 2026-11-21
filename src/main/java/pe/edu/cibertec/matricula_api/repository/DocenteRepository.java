package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cibertec.matricula_api.model.Docente;

import java.util.List;
import java.util.Optional;

public interface DocenteRepository extends JpaRepository<Docente, Long> {

    Optional<Docente> findByCodigo(String codigo);

    Optional<Docente> findByEmail(String email);

    boolean existsByCodigo(String codigo);

    @Query("""
            select d from Docente d
            where :nombre is null or lower(concat(d.nombres, ' ', d.apellidos)) like lower(concat('%', :nombre, '%'))
            order by d.codigo
            """)
    List<Docente> buscar(@Param("nombre") String nombre);
}
