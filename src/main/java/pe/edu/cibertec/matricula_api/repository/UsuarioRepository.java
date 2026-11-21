package pe.edu.cibertec.matricula_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.cibertec.matricula_api.model.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);

    boolean existsByUsername(String username);

    List<Usuario> findAllByOrderByUsername();
}
