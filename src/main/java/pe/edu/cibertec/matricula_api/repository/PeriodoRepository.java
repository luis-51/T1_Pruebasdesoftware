package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.matricula_api.model.EstadoPeriodo;
import pe.edu.cibertec.matricula_api.model.Periodo;

import java.util.List;
import java.util.Optional;

public interface PeriodoRepository extends JpaRepository<Periodo, Long> {

    Optional<Periodo> findByCodigo(String codigo);

    Optional<Periodo> findFirstByEstado(EstadoPeriodo estado);

    boolean existsByCodigo(String codigo);

    List<Periodo> findAllByOrderByCodigoDesc();
}
