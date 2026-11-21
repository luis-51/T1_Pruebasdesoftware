package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Periodo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 6)
    private String codigo;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    @Enumerated(EnumType.STRING)
    private EstadoPeriodo estado;

    protected Periodo() {
    }

    public Periodo(String codigo, LocalDate fechaInicio, LocalDate fechaFin) {
        this.codigo = codigo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = EstadoPeriodo.CERRADO;
    }

    public boolean abierto() {
        return estado == EstadoPeriodo.ABIERTO;
    }

    public void abrir() {
        estado = EstadoPeriodo.ABIERTO;
    }

    public void cerrar() {
        estado = EstadoPeriodo.CERRADO;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public LocalDate getFechaInicio() { return fechaInicio; }
    public LocalDate getFechaFin() { return fechaFin; }
    public EstadoPeriodo getEstado() { return estado; }
}
