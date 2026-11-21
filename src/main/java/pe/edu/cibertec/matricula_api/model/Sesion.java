package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Sesion {

    @Id
    @Column(length = 36)
    private String id;

    @ManyToOne(optional = false)
    private Usuario usuario;

    @Column(unique = true, nullable = false, length = 64)
    private String refreshHash;

    private LocalDateTime creada;
    private LocalDateTime expira;
    private boolean revocada;

    protected Sesion() {
    }

    public Sesion(Usuario usuario, String refreshHash, LocalDateTime expira) {
        this.id = UUID.randomUUID().toString();
        this.usuario = usuario;
        this.refreshHash = refreshHash;
        this.creada = LocalDateTime.now();
        this.expira = expira;
    }

    public boolean vencida() {
        return expira.isBefore(LocalDateTime.now());
    }

    public void revocar() {
        revocada = true;
    }

    public String getId() { return id; }
    public Usuario getUsuario() { return usuario; }
    public LocalDateTime getCreada() { return creada; }
    public LocalDateTime getExpira() { return expira; }
    public boolean isRevocada() { return revocada; }
}
