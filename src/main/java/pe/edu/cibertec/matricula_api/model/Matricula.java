package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Matricula {

    public static final double NOTA_APROBATORIA = 13;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Alumno alumno;

    @ManyToOne(optional = false)
    private Curso curso;

    @ManyToOne(optional = false)
    private Periodo periodo;

    private double monto;

    @Enumerated(EnumType.STRING)
    private EstadoMatricula estado;

    private Double nota;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaPago;

    protected Matricula() {
    }

    public Matricula(Alumno alumno, Curso curso, Periodo periodo, double monto) {
        this.alumno = alumno;
        this.curso = curso;
        this.periodo = periodo;
        this.monto = monto;
        this.estado = EstadoMatricula.REGISTRADA;
        this.fechaRegistro = LocalDateTime.now();
    }

    public void pagar() {
        estado = EstadoMatricula.PAGADA;
        fechaPago = LocalDateTime.now();
    }

    public void anular() {
        estado = EstadoMatricula.ANULADA;
    }

    public void calificar(double nota) {
        this.nota = nota;
    }

    public String condicion() {
        if (nota == null) return null;
        return nota >= NOTA_APROBATORIA ? "APROBADO" : "DESAPROBADO";
    }

    public Long getId() { return id; }
    public Alumno getAlumno() { return alumno; }
    public Curso getCurso() { return curso; }
    public Periodo getPeriodo() { return periodo; }
    public double getMonto() { return monto; }
    public EstadoMatricula getEstado() { return estado; }
    public Double getNota() { return nota; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public LocalDateTime getFechaPago() { return fechaPago; }
}
