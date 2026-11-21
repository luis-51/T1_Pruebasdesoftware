package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.matricula_api.model.Carrera;

import java.util.List;
import java.util.Optional;

public interface CarreraRepository extends JpaRepository<Carrera, Long> {

    Optional<Carrera> findByCodigo(String codigo);

    boolean existsByCodigo(String codigo);

    List<Carrera> findAllByOrderByCodigo();
}
