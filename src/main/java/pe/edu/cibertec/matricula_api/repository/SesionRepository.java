package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.cibertec.matricula_api.model.Sesion;
import pe.edu.cibertec.matricula_api.model.Usuario;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SesionRepository extends JpaRepository<Sesion, String> {

    Optional<Sesion> findByRefreshHash(String refreshHash);

    boolean existsByIdAndRevocadaFalseAndExpiraAfter(String id, LocalDateTime ahora);

    @Modifying
    @Query("update Sesion s set s.revocada = true where s.usuario = :usuario and s.revocada = false")
    int revocarTodas(@Param("usuario") Usuario usuario);

    @Modifying
    @Query("delete from Sesion s where s.usuario = :usuario")
    void eliminarDe(@Param("usuario") Usuario usuario);
}
