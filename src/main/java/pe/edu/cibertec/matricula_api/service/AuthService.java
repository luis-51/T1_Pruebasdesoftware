package pe.edu.cibertec.matricula_api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.*;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.Alumno;
import pe.edu.cibertec.matricula_api.model.Docente;
import pe.edu.cibertec.matricula_api.model.Sesion;
import pe.edu.cibertec.matricula_api.model.Usuario;
import pe.edu.cibertec.matricula_api.repository.AlumnoRepository;
import pe.edu.cibertec.matricula_api.repository.DocenteRepository;
import pe.edu.cibertec.matricula_api.repository.SesionRepository;
import pe.edu.cibertec.matricula_api.repository.UsuarioRepository;
import pe.edu.cibertec.matricula_api.security.TokenConfig;
import pe.edu.cibertec.matricula_api.security.UsuarioActual;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final SesionRepository sesiones;
    private final AlumnoRepository alumnos;
    private final DocenteRepository docentes;
    private final PasswordEncoder encoder;
    private final JwtEncoder jwtEncoder;
    private final UsuarioActual actual;
    private final SecureRandom random = new SecureRandom();
    private final String hashFalso;

    @Value("${seguridad.access-segundos}")
    private long accessSegundos;

    @Value("${seguridad.refresh-horas}")
    private long refreshHoras;

    @Value("${seguridad.max-intentos}")
    private int maxIntentos;

    @Value("${seguridad.bloqueo-minutos}")
    private long bloqueoMinutos;

    public AuthService(UsuarioRepository usuarios, SesionRepository sesiones, AlumnoRepository alumnos,
                       DocenteRepository docentes, PasswordEncoder encoder, JwtEncoder jwtEncoder,
                       UsuarioActual actual) {
        this.usuarios = usuarios;
        this.sesiones = sesiones;
        this.alumnos = alumnos;
        this.docentes = docentes;
        this.encoder = encoder;
        this.jwtEncoder = jwtEncoder;
        this.actual = actual;
        this.hashFalso = encoder.encode(UUID.randomUUID().toString());
    }

    @Transactional(noRollbackFor = ApiException.class)
    public TokenResponse login(LoginRequest req) {
        Usuario u = usuarios.findByUsername(req.username()).orElse(null);
        if (u == null) {
            encoder.matches(req.password(), hashFalso);
            throw credencialesInvalidas();
        }
        if (u.bloqueado()) throw cuentaBloqueada(u);
        if (!encoder.matches(req.password(), u.getPassword())) {
            u.registrarFallo(maxIntentos, Duration.ofMinutes(bloqueoMinutos));
            if (u.bloqueado()) throw cuentaBloqueada(u);
            throw credencialesInvalidas();
        }
        u.desbloquear();
        return emitir(u);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public TokenResponse refresh(RefreshRequest req) {
        Sesion s = sesiones.findByRefreshHash(sha256(req.refreshToken()))
                .orElseThrow(() -> ApiException.noAutenticado("REFRESH_INVALIDO", "El refresh token no es válido"));
        if (s.isRevocada()) {
            sesiones.revocarTodas(s.getUsuario());
            throw ApiException.noAutenticado("REFRESH_REUTILIZADO",
                    "El refresh token ya fue usado; por seguridad se cerraron todas las sesiones del usuario");
        }
        if (s.vencida()) {
            throw ApiException.noAutenticado("REFRESH_INVALIDO", "El refresh token venció");
        }
        if (s.getUsuario().bloqueado()) throw cuentaBloqueada(s.getUsuario());
        s.revocar();
        return emitir(s.getUsuario());
    }

    @Transactional
    public void logout() {
        sesiones.findById(actual.sesion()).ifPresent(Sesion::revocar);
    }

    @Transactional
    public void cambiarPassword(PasswordRequest req) {
        Usuario u = usuarios.findByUsername(actual.username()).orElseThrow(ApiException::prohibido);
        if (!encoder.matches(req.actual(), u.getPassword())) {
            throw ApiException.noProcesable("PASSWORD_INCORRECTA", "La contraseña actual no es correcta");
        }
        if (req.actual().equals(req.nueva())) {
            throw ApiException.noProcesable("PASSWORD_REPETIDA", "La nueva contraseña debe ser distinta de la actual");
        }
        u.cambiarPassword(encoder.encode(req.nueva()));
        sesiones.revocarTodas(u);
    }

    public PerfilResponse perfil() {
        String username = actual.username();
        String nombre = switch (actual.rol()) {
            case ADMIN -> "Administrador";
            case ALUMNO -> alumnos.findByCodigo(username).map(Alumno::nombreCompleto).orElse(username);
            case DOCENTE -> docentes.findByCodigo(username).map(Docente::nombreCompleto).orElse(username);
        };
        return new PerfilResponse(username, actual.rol(), nombre);
    }

    private TokenResponse emitir(Usuario u) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String refresh = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        Sesion s = sesiones.save(new Sesion(u, sha256(refresh), LocalDateTime.now().plusHours(refreshHoras)));

        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(TokenConfig.EMISOR)
                .subject(u.getUsername())
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(accessSegundos))
                .id(UUID.randomUUID().toString())
                .claim("rol", u.getRol().name())
                .claim("sid", s.getId())
                .build();
        String access = jwtEncoder.encode(
                JwtEncoderParameters.from(JwsHeader.with(SignatureAlgorithm.RS256).build(), claims)).getTokenValue();
        return new TokenResponse(access, refresh, "Bearer", accessSegundos, u.getUsername(), u.getRol());
    }

    private static ApiException credencialesInvalidas() {
        return ApiException.noAutenticado("CREDENCIALES_INVALIDAS", "Usuario o contraseña incorrectos");
    }

    private static ApiException cuentaBloqueada(Usuario u) {
        return ApiException.bloqueado("Cuenta bloqueada por intentos fallidos hasta las "
                + u.getBloqueadoHasta().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private static String sha256(String valor) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
