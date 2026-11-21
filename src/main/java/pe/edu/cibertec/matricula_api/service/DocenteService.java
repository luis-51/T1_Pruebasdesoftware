package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.DocenteRequest;
import pe.edu.cibertec.matricula_api.dto.DocenteResponse;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.Docente;
import pe.edu.cibertec.matricula_api.model.Rol;
import pe.edu.cibertec.matricula_api.repository.CursoRepository;
import pe.edu.cibertec.matricula_api.repository.DocenteRepository;
import pe.edu.cibertec.matricula_api.security.UsuarioActual;

import java.util.List;

@Service
public class DocenteService {

    private final DocenteRepository docentes;
    private final CursoRepository cursos;
    private final UsuarioService usuarioService;
    private final UsuarioActual actual;

    public DocenteService(DocenteRepository docentes, CursoRepository cursos, UsuarioService usuarioService,
                          UsuarioActual actual) {
        this.docentes = docentes;
        this.cursos = cursos;
        this.usuarioService = usuarioService;
        this.actual = actual;
    }

    public List<DocenteResponse> listar(String nombre) {
        return docentes.buscar(nombre).stream().map(DocenteResponse::of).toList();
    }

    public DocenteResponse obtener(String codigo) {
        actual.permitirDocente(codigo);
        return DocenteResponse.of(buscar(codigo));
    }

    @Transactional
    public DocenteResponse crear(DocenteRequest req) {
        if (docentes.existsByCodigo(req.codigo())) {
            throw ApiException.conflicto("DOCENTE_DUPLICADO", "Ya existe el docente " + req.codigo());
        }
        validarEmail(req.email(), null);
        Docente d = docentes.save(new Docente(req.codigo(), req.nombres(), req.apellidos(), req.email(),
                req.especialidad()));
        return DocenteResponse.of(d, usuarioService.crearCuenta(d.getCodigo(), Rol.DOCENTE));
    }

    @Transactional
    public DocenteResponse actualizar(String codigo, DocenteRequest req) {
        Docente d = buscar(codigo);
        validarEmail(req.email(), d);
        d.actualizar(req.nombres(), req.apellidos(), req.email(), req.especialidad());
        return DocenteResponse.of(d);
    }

    @Transactional
    public void eliminar(String codigo) {
        Docente d = buscar(codigo);
        if (cursos.existsByDocente(d)) {
            throw ApiException.conflicto("DOCENTE_CON_CURSOS", "El docente " + codigo + " tiene cursos asignados");
        }
        usuarioService.eliminarCuenta(codigo);
        docentes.delete(d);
    }

    Docente buscar(String codigo) {
        return docentes.findByCodigo(codigo)
                .orElseThrow(() -> ApiException.noEncontrado("DOCENTE_NO_ENCONTRADO", "No existe el docente " + codigo));
    }

    private void validarEmail(String email, Docente actual) {
        docentes.findByEmail(email)
                .filter(otro -> actual == null || !otro.getId().equals(actual.getId()))
                .ifPresent(otro -> {
                    throw ApiException.conflicto("EMAIL_DUPLICADO", "El email " + email + " ya está registrado");
                });
    }
}
