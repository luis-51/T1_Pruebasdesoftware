package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

@Entity
public class Alumno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 12)
    private String codigo;

    private String nombres;
    private String apellidos;

    @Column(unique = true)
    private String email;

    private double promedio;
    private boolean tieneDeuda;

    @ManyToOne(optional = false)
    private Carrera carrera;

    protected Alumno() {
    }

    public Alumno(String codigo, String nombres, String apellidos, String email, double promedio,
                  boolean tieneDeuda, Carrera carrera) {
        this.codigo = codigo;
        actualizar(nombres, apellidos, email, promedio, tieneDeuda, carrera);
    }

    public void actualizar(String nombres, String apellidos, String email, double promedio,
                           boolean tieneDeuda, Carrera carrera) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.promedio = promedio;
        this.tieneDeuda = tieneDeuda;
        this.carrera = carrera;
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public double getPromedio() { return promedio; }
    public boolean isTieneDeuda() { return tieneDeuda; }
    public Carrera getCarrera() { return carrera; }
}
