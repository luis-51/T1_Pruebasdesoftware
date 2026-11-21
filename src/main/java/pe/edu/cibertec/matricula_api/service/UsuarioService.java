package pe.edu.cibertec.matricula_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.UsuarioResponse;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.Rol;
import pe.edu.cibertec.matricula_api.model.Usuario;
import pe.edu.cibertec.matricula_api.repository.SesionRepository;
import pe.edu.cibertec.matricula_api.repository.UsuarioRepository;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private static final String MAYUSCULAS = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String MINUSCULAS = "abcdefghijkmnpqrstuvwxyz";
    private static final String DIGITOS = "23456789";

    private final UsuarioRepository usuarios;
    private final SesionRepository sesiones;
    private final PasswordEncoder encoder;
    private final SecureRandom random = new SecureRandom();

    public UsuarioService(UsuarioRepository usuarios, SesionRepository sesiones, PasswordEncoder encoder) {
        this.usuarios = usuarios;
        this.sesiones = sesiones;
        this.encoder = encoder;
    }

    public List<UsuarioResponse> listar() {
        return usuarios.findAllByOrderByUsername().stream().map(UsuarioResponse::of).toList();
    }

    @Transactional
    public UsuarioResponse desbloquear(String username) {
        Usuario u = usuarios.findByUsername(username)
                .orElseThrow(() -> ApiException.noEncontrado("USUARIO_NO_ENCONTRADO", "No existe el usuario " + username));
        u.desbloquear();
        return UsuarioResponse.of(u);
    }

    String crearCuenta(String username, Rol rol) {
        String clave = claveTemporal();
        usuarios.save(new Usuario(username, encoder.encode(clave), rol));
        return clave;
    }

    void eliminarCuenta(String username) {
        usuarios.findByUsername(username).ifPresent(u -> {
            sesiones.eliminarDe(u);
            usuarios.delete(u);
        });
    }

    private String claveTemporal() {
        List<Character> c = new ArrayList<>(List.of(elegir(MAYUSCULAS), elegir(MINUSCULAS), elegir(DIGITOS)));
        String todos = MAYUSCULAS + MINUSCULAS + DIGITOS;
        for (int i = 0; i < 9; i++) c.add(elegir(todos));
        Collections.shuffle(c, random);
        return c.stream().map(String::valueOf).collect(Collectors.joining());
    }

    private char elegir(String fuente) {
        return fuente.charAt(random.nextInt(fuente.length()));
    }
}
