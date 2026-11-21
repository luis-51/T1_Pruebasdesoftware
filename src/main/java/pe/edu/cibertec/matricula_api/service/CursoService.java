package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.AlumnoMatriculadoResponse;
import pe.edu.cibertec.matricula_api.dto.CursoRequest;
import pe.edu.cibertec.matricula_api.dto.CursoResponse;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.*;
import pe.edu.cibertec.matricula_api.repository.CursoRepository;
import pe.edu.cibertec.matricula_api.repository.MatriculaRepository;
import pe.edu.cibertec.matricula_api.security.UsuarioActual;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pe.edu.cibertec.matricula_api.model.EstadoMatricula.ANULADA;

@Service
public class CursoService {

    private final CursoRepository cursos;
    private final MatriculaRepository matriculas;
    private final CarreraService carreraService;
    private final DocenteService docenteService;
    private final PeriodoService periodoService;
    private final UsuarioActual actual;

    public CursoService(CursoRepository cursos, MatriculaRepository matriculas, CarreraService carreraService,
                        DocenteService docenteService, PeriodoService periodoService, UsuarioActual actual) {
        this.cursos = cursos;
        this.matriculas = matriculas;
        this.carreraService = carreraService;
        this.docenteService = docenteService;
        this.periodoService = periodoService;
        this.actual = actual;
    }

    public List<CursoResponse> listar(Integer ciclo, Modalidad modalidad, DiaSemana dia, String carrera, String nombre) {
        return cursos.buscar(ciclo, modalidad, dia, carrera, nombre).stream().map(this::toResponse).toList();
    }

    public CursoResponse obtener(String codigo) {
        return toResponse(buscar(codigo));
    }

    public List<CursoResponse> deDocente(String codigoDocente) {
        actual.permitirDocente(codigoDocente);
        Docente d = docenteService.buscar(codigoDocente);
        return cursos.findByDocenteOrderByCodigo(d).stream().map(this::toResponse).toList();
    }

    public List<AlumnoMatriculadoResponse> alumnos(String codigo) {
        Curso curso = buscar(codigo);
        Docente d = curso.getDocente();
        if (!actual.esAdmin() && (d == null || !actual.es(Rol.DOCENTE, d.getCodigo()))) {
            throw ApiException.prohibido();
        }
        return periodoService.abierto()
                .map(p -> matriculas.findByCursoAndPeriodoAndEstadoNotOrderById(curso, p, ANULADA))
                .orElse(List.of())
                .stream().map(AlumnoMatriculadoResponse::of).toList();
    }

    @Transactional
    public CursoResponse crear(CursoRequest req) {
        if (cursos.existsByCodigo(req.codigo())) {
            throw ApiException.conflicto("CURSO_DUPLICADO", "Ya existe el curso " + req.codigo());
        }
        validarHorario(req);
        Curso curso = new Curso(req.codigo(), req.nombre(), req.ciclo(), req.creditos(), req.vacantes(),
                req.modalidad(), req.dia(), req.horaInicio(), req.horaFin());
        asignar(curso, req);
        return toResponse(cursos.save(curso));
    }

    @Transactional
    public CursoResponse actualizar(String codigo, CursoRequest req) {
        Curso curso = buscar(codigo);
        validarHorario(req);
        int ocupadas = ocupadas(curso);
        if (req.vacantes() < ocupadas) {
            throw ApiException.noProcesable("VACANTES_INSUFICIENTES",
                    "El curso ya tiene " + ocupadas + " matriculados");
        }
        curso.actualizar(req.nombre(), req.ciclo(), req.creditos(), req.vacantes(),
                req.modalidad(), req.dia(), req.horaInicio(), req.horaFin());
        asignar(curso, req);
        return CursoResponse.of(curso, ocupadas);
    }

    @Transactional
    public void eliminar(String codigo) {
        Curso curso = buscar(codigo);
        if (matriculas.existsByCurso(curso)) {
            throw ApiException.conflicto("CURSO_CON_MATRICULAS",
                    "El curso " + codigo + " tiene matrículas registradas");
        }
        if (cursos.esRequisito(curso)) {
            throw ApiException.conflicto("CURSO_ES_REQUISITO", "El curso " + codigo + " es requisito de otro curso");
        }
        cursos.delete(curso);
    }

    Curso buscar(String codigo) {
        return cursos.findByCodigo(codigo)
                .orElseThrow(() -> ApiException.noEncontrado("CURSO_NO_ENCONTRADO",
                        "No existe el curso " + codigo));
    }

    Curso bloquear(String codigo) {
        return cursos.bloquear(codigo)
                .orElseThrow(() -> ApiException.noEncontrado("CURSO_NO_ENCONTRADO",
                        "No existe el curso " + codigo));
    }

    int ocupadas(Curso curso) {
        return periodoService.abierto()
                .map(p -> matriculas.countByCursoAndPeriodoAndEstadoNot(curso, p, ANULADA))
                .orElse(0);
    }

    private void asignar(Curso curso, CursoRequest req) {
        Carrera carrera = carreraService.buscar(req.codigoCarrera());
        Docente docente = req.codigoDocente() == null || req.codigoDocente().isBlank()
                ? null : docenteService.buscar(req.codigoDocente());
        Set<Curso> requisitos = new HashSet<>();
        for (String cod : req.requisitosOVacio()) {
            if (cod.equals(curso.getCodigo())) {
                throw ApiException.noProcesable("REQUISITO_INVALIDO", "Un curso no puede ser requisito de sí mismo");
            }
            requisitos.add(cursos.findByCodigo(cod).orElseThrow(() ->
                    ApiException.noProcesable("REQUISITO_INVALIDO", "No existe el curso requisito " + cod)));
        }
        curso.asignar(carrera, docente, requisitos);
    }

    private void validarHorario(CursoRequest req) {
        if (!req.horaInicio().isBefore(req.horaFin())) {
            throw ApiException.invalido("HORARIO_INVALIDO", "La hora de inicio debe ser anterior a la hora de fin");
        }
    }

    private CursoResponse toResponse(Curso c) {
        return CursoResponse.of(c, ocupadas(c));
    }
}
