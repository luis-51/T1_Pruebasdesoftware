package pe.edu.cibertec.matricula_api.model;

import jakarta.persistence.*;

@Entity
public class Docente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 5)
    private String codigo;

    private String nombres;
    private String apellidos;

    @Column(unique = true)
    private String email;

    private String especialidad;

    protected Docente() {
    }

    public Docente(String codigo, String nombres, String apellidos, String email, String especialidad) {
        this.codigo = codigo;
        actualizar(nombres, apellidos, email, especialidad);
    }

    public void actualizar(String nombres, String apellidos, String email, String especialidad) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.especialidad = especialidad;
    }

    public String nombreCompleto() {
        return nombres + " " + apellidos;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getEmail() { return email; }
    public String getEspecialidad() { return especialidad; }
}
