package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 20)
    private String username;

    @Column(nullable = false, length = 72)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    private int intentosFallidos;
    private LocalDateTime bloqueadoHasta;

    protected Usuario() {
    }

    public Usuario(String username, String password, Rol rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public boolean bloqueado() {
        return bloqueadoHasta != null && bloqueadoHasta.isAfter(LocalDateTime.now());
    }

    public void registrarFallo(int maxIntentos, Duration bloqueo) {
        intentosFallidos++;
        if (intentosFallidos >= maxIntentos) {
            bloqueadoHasta = LocalDateTime.now().plus(bloqueo);
            intentosFallidos = 0;
        }
    }

    public void desbloquear() {
        intentosFallidos = 0;
        bloqueadoHasta = null;
    }

    public void cambiarPassword(String password) {
        this.password = password;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public Rol getRol() { return rol; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public LocalDateTime getBloqueadoHasta() { return bloqueadoHasta; }
}
