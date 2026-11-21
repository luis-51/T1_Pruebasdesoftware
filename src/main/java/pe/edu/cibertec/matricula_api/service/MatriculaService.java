package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.*;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.*;
import pe.edu.cibertec.matricula_api.repository.MatriculaRepository;
import pe.edu.cibertec.matricula_api.repository.PagoRepository;
import pe.edu.cibertec.matricula_api.security.UsuarioActual;

import java.util.List;

import static pe.edu.cibertec.matricula_api.model.EstadoMatricula.*;
import static pe.edu.cibertec.matricula_api.model.Matricula.NOTA_APROBATORIA;
import static pe.edu.cibertec.matricula_api.service.CalculadoraMatricula.MAX_CREDITOS;
import static pe.edu.cibertec.matricula_api.service.CalculadoraMatricula.redondear;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculas;
    private final PagoRepository pagos;
    private final AlumnoService alumnoService;
    private final CursoService cursoService;
    private final PeriodoService periodoService;
    private final CalculadoraMatricula calculadora;
    private final UsuarioActual actual;

    public MatriculaService(MatriculaRepository matriculas, PagoRepository pagos, AlumnoService alumnoService,
                            CursoService cursoService, PeriodoService periodoService,
                            CalculadoraMatricula calculadora, UsuarioActual actual) {
        this.matriculas = matriculas;
        this.pagos = pagos;
        this.alumnoService = alumnoService;
        this.cursoService = cursoService;
        this.periodoService = periodoService;
        this.calculadora = calculadora;
        this.actual = actual;
    }

    public CotizacionResponse cotizar(CotizacionRequest req) {
        actual.permitirAlumno(req.codigoAlumno());
        Alumno alumno = alumnoService.buscar(req.codigoAlumno());
        List<Curso> cursos = req.cursos().stream().distinct().map(cursoService::buscar).toList();

        int creditos = cursos.stream().mapToInt(Curso::getCreditos).sum();
        List<Matricula> vigentes = periodoService.abierto().map(p -> vigentes(alumno, p)).orElse(List.of());
        validarLimite(creditosDe(vigentes) + creditos);

        return new CotizacionResponse(
                alumno.getCodigo(),
                cursos.stream().map(Curso::getCodigo).toList(),
                creditos,
                calculadora.subtotal(creditos),
                porcentaje(alumno),
                calculadora.monto(creditos, alumno.getPromedio()));
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public MatriculaResponse registrar(MatriculaRequest req) {
        actual.permitirAlumno(req.codigoAlumno());
        Alumno alumno = alumnoService.buscar(req.codigoAlumno());
        Curso curso = cursoService.bloquear(req.codigoCurso());
        Periodo periodo = periodoService.abiertoOFalla();

        if (!curso.getCarrera().getId().equals(alumno.getCarrera().getId())) {
            throw ApiException.noProcesable("CURSO_DE_OTRA_CARRERA", "El curso " + curso.getCodigo()
                    + " pertenece a la carrera " + curso.getCarrera().getCodigo());
        }
        if (alumno.isTieneDeuda()) {
            throw ApiException.noProcesable("ALUMNO_CON_DEUDA",
                    "El alumno " + alumno.getCodigo() + " tiene deuda pendiente");
        }
        List<Matricula> vigentes = vigentes(alumno, periodo);
        if (vigentes.stream().anyMatch(m -> m.getCurso().getId().equals(curso.getId()))) {
            throw ApiException.conflicto("MATRICULA_DUPLICADA",
                    "El alumno ya está matriculado en " + curso.getCodigo());
        }
        if (aprobo(alumno, curso)) {
            throw ApiException.noProcesable("CURSO_APROBADO", "El alumno ya aprobó " + curso.getCodigo());
        }
        curso.requisitosOrdenados().stream()
                .filter(r -> !aprobo(alumno, r))
                .findFirst()
                .ifPresent(r -> {
                    throw ApiException.noProcesable("PRERREQUISITO_PENDIENTE",
                            "Para llevar " + curso.getCodigo() + " debe aprobar " + r.getCodigo());
                });
        if (cursoService.ocupadas(curso) >= curso.getVacantes()) {
            throw ApiException.conflicto("SIN_VACANTES",
                    "El curso " + curso.getCodigo() + " no tiene vacantes");
        }
        vigentes.stream()
                .map(Matricula::getCurso)
                .filter(curso::seCruzaCon)
                .findFirst()
                .ifPresent(otro -> {
                    throw ApiException.conflicto("CRUCE_HORARIO", "El horario de " + curso.getCodigo()
                            + " se cruza con " + otro.getCodigo() + " (" + otro.horario() + ")");
                });
        validarLimite(creditosDe(vigentes) + curso.getCreditos());

        double monto = calculadora.monto(curso.getCreditos(), alumno.getPromedio());
        return MatriculaResponse.of(matriculas.save(new Matricula(alumno, curso, periodo, monto)));
    }

    public List<MatriculaResponse> listar(String alumno, String curso, EstadoMatricula estado, String periodo) {
        String filtro = actual.esAdmin() ? alumno : actual.username();
        return matriculas.buscar(filtro, curso, estado, periodo).stream().map(MatriculaResponse::of).toList();
    }

    public MatriculaResponse obtener(Long id) {
        Matricula m = buscar(id);
        Docente d = m.getCurso().getDocente();
        boolean esSuDocente = d != null && actual.es(Rol.DOCENTE, d.getCodigo());
        if (!actual.esAdmin() && !actual.es(Rol.ALUMNO, m.getAlumno().getCodigo()) && !esSuDocente) {
            throw ApiException.prohibido();
        }
        return MatriculaResponse.of(m);
    }

    @Transactional
    public MatriculaResponse pagar(Long id, PagoRequest req) {
        Matricula m = buscar(id);
        actual.permitirAlumno(m.getAlumno().getCodigo());
        if (m.getEstado() == ANULADA) {
            throw ApiException.conflicto("MATRICULA_ANULADA", "La matrícula " + id + " está anulada");
        }
        if (m.getEstado() == PAGADA) {
            throw ApiException.conflicto("MATRICULA_YA_PAGADA", "La matrícula " + id + " ya fue pagada");
        }
        m.pagar();
        pagos.save(new Pago(m, req == null ? MetodoPago.EFECTIVO : req.metodoOEfectivo()));
        return MatriculaResponse.of(m);
    }

    @Transactional
    public MatriculaResponse anular(Long id) {
        Matricula m = buscar(id);
        actual.permitirAlumno(m.getAlumno().getCodigo());
        if (m.getEstado() == ANULADA) {
            throw ApiException.conflicto("MATRICULA_YA_ANULADA", "La matrícula " + id + " ya fue anulada");
        }
        if (m.getEstado() == PAGADA) {
            throw ApiException.conflicto("MATRICULA_PAGADA",
                    "La matrícula " + id + " ya fue pagada y no se puede anular");
        }
        m.anular();
        return MatriculaResponse.of(m);
    }

    @Transactional
    public MatriculaResponse calificar(Long id, NotaRequest req) {
        Matricula m = buscar(id);
        Docente d = m.getCurso().getDocente();
        if (!actual.esAdmin() && (d == null || !actual.es(Rol.DOCENTE, d.getCodigo()))) {
            throw ApiException.prohibido();
        }
        if (m.getEstado() != PAGADA) {
            throw ApiException.conflicto("MATRICULA_NO_PAGADA", "Solo se califican matrículas pagadas");
        }
        m.calificar(req.nota());
        return MatriculaResponse.of(m);
    }

    public ConstanciaResponse constancia(String codigoAlumno) {
        actual.permitirAlumno(codigoAlumno);
        Alumno alumno = alumnoService.buscar(codigoAlumno);
        Periodo periodo = periodoService.abiertoOFalla();
        List<Matricula> vigentes = vigentes(alumno, periodo);

        double total = vigentes.stream().mapToDouble(Matricula::getMonto).sum();
        double pagado = vigentes.stream()
                .filter(m -> m.getEstado() == PAGADA)
                .mapToDouble(Matricula::getMonto)
                .sum();

        return new ConstanciaResponse(
                alumno.getCodigo(),
                alumno.nombreCompleto(),
                periodo.getCodigo(),
                alumno.getPromedio(),
                porcentaje(alumno),
                vigentes.stream().map(MatriculaResponse::of).toList(),
                creditosDe(vigentes),
                redondear(total),
                redondear(pagado),
                redondear(total - pagado));
    }

    public HistorialResponse historial(String codigoAlumno) {
        actual.permitirAlumno(codigoAlumno);
        Alumno alumno = alumnoService.buscar(codigoAlumno);
        List<Matricula> notas = matriculas.findByAlumnoAndNotaIsNotNullOrderByPeriodoCodigoAscCursoCodigoAsc(alumno);

        int creditos = notas.stream().mapToInt(m -> m.getCurso().getCreditos()).sum();
        double suma = notas.stream().mapToDouble(m -> m.getNota() * m.getCurso().getCreditos()).sum();
        int aprobados = notas.stream()
                .filter(m -> m.getNota() >= NOTA_APROBATORIA)
                .mapToInt(m -> m.getCurso().getCreditos())
                .sum();

        return new HistorialResponse(
                alumno.getCodigo(),
                alumno.nombreCompleto(),
                alumno.getCarrera().getCodigo(),
                notas.stream().map(m -> new HistorialResponse.Item(m.getPeriodo().getCodigo(),
                        m.getCurso().getCodigo(), m.getCurso().getNombre(), m.getCurso().getCreditos(),
                        m.getNota(), m.condicion())).toList(),
                aprobados,
                creditos == 0 ? 0 : redondear(suma / creditos));
    }

    private List<Matricula> vigentes(Alumno alumno, Periodo periodo) {
        return matriculas.findByAlumnoAndPeriodoAndEstadoNotOrderById(alumno, periodo, ANULADA);
    }

    private boolean aprobo(Alumno alumno, Curso curso) {
        return matriculas.existsByAlumnoAndCursoAndNotaGreaterThanEqual(alumno, curso, NOTA_APROBATORIA);
    }

    private int creditosDe(List<Matricula> matriculas) {
        return matriculas.stream().mapToInt(m -> m.getCurso().getCreditos()).sum();
    }

    private int porcentaje(Alumno alumno) {
        return (int) Math.round(calculadora.descuento(alumno.getPromedio()) * 100);
    }

    private void validarLimite(int creditos) {
        if (creditos > MAX_CREDITOS) {
            throw ApiException.noProcesable("LIMITE_CREDITOS",
                    "El alumno sumaría " + creditos + " créditos; el máximo es " + MAX_CREDITOS);
        }
    }

    private Matricula buscar(Long id) {
        return matriculas.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("MATRICULA_NO_ENCONTRADA",
                        "No existe la matrícula " + id));
    }
}
