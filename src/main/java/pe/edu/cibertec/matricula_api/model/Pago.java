package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(unique = true)
    private Matricula matricula;

    private double monto;

    @Enumerated(EnumType.STRING)
    private MetodoPago metodo;

    private LocalDateTime fecha;

    protected Pago() {
    }

    public Pago(Matricula matricula, MetodoPago metodo) {
        this.matricula = matricula;
        this.monto = matricula.getMonto();
        this.metodo = metodo;
        this.fecha = LocalDateTime.now();
    }

    public String numeroOperacion() {
        return "OP-%06d".formatted(id);
    }

    public Long getId() { return id; }
    public Matricula getMatricula() { return matricula; }
    public double getMonto() { return monto; }
    public MetodoPago getMetodo() { return metodo; }
    public LocalDateTime getFecha() { return fecha; }
}
