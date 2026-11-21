package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cibertec.matricula_api.model.MetodoPago;
import pe.edu.cibertec.matricula_api.model.Pago;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    @Query("""
            select p from Pago p
            where (:alumno is null or p.matricula.alumno.codigo = :alumno)
              and (:metodo is null or p.metodo = :metodo)
            order by p.id
            """)
    List<Pago> buscar(@Param("alumno") String alumno, @Param("metodo") MetodoPago metodo);
}
