package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.PeriodoRequest;
import pe.edu.cibertec.matricula_api.dto.PeriodoResponse;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.Matricula;
import pe.edu.cibertec.matricula_api.model.Periodo;
import pe.edu.cibertec.matricula_api.repository.MatriculaRepository;
import pe.edu.cibertec.matricula_api.repository.PeriodoRepository;

import java.util.List;
import java.util.Optional;

import static pe.edu.cibertec.matricula_api.model.EstadoMatricula.REGISTRADA;
import static pe.edu.cibertec.matricula_api.model.EstadoPeriodo.ABIERTO;

@Service
public class PeriodoService {

    private final PeriodoRepository periodos;
    private final MatriculaRepository matriculas;

    public PeriodoService(PeriodoRepository periodos, MatriculaRepository matriculas) {
        this.periodos = periodos;
        this.matriculas = matriculas;
    }

    public List<PeriodoResponse> listar() {
        return periodos.findAllByOrderByCodigoDesc().stream().map(PeriodoResponse::of).toList();
    }

    public PeriodoResponse obtener(String codigo) {
        return PeriodoResponse.of(buscar(codigo));
    }

    public PeriodoResponse actual() {
        return abierto().map(PeriodoResponse::of)
                .orElseThrow(() -> ApiException.noEncontrado("SIN_PERIODO_ABIERTO", "No hay un periodo abierto"));
    }

    @Transactional
    public PeriodoResponse crear(PeriodoRequest req) {
        if (periodos.existsByCodigo(req.codigo())) {
            throw ApiException.conflicto("PERIODO_DUPLICADO", "Ya existe el periodo " + req.codigo());
        }
        if (!req.fechaInicio().isBefore(req.fechaFin())) {
            throw ApiException.invalido("FECHAS_INVALIDAS", "La fecha de inicio debe ser anterior a la fecha de fin");
        }
        return PeriodoResponse.of(periodos.save(new Periodo(req.codigo(), req.fechaInicio(), req.fechaFin())));
    }

    @Transactional
    public PeriodoResponse abrir(String codigo) {
        Periodo p = buscar(codigo);
        if (p.abierto()) {
            throw ApiException.conflicto("PERIODO_YA_ABIERTO", "El periodo " + codigo + " ya está abierto");
        }
        abierto().ifPresent(otro -> {
            throw ApiException.conflicto("PERIODO_ABIERTO_EXISTENTE",
                    "Primero cierre el periodo " + otro.getCodigo());
        });
        p.abrir();
        return PeriodoResponse.of(p);
    }

    @Transactional
    public PeriodoResponse cerrar(String codigo) {
        Periodo p = buscar(codigo);
        if (!p.abierto()) {
            throw ApiException.conflicto("PERIODO_YA_CERRADO", "El periodo " + codigo + " ya está cerrado");
        }
        matriculas.findByPeriodoAndEstado(p, REGISTRADA).forEach(Matricula::anular);
        p.cerrar();
        return PeriodoResponse.of(p);
    }

    Optional<Periodo> abierto() {
        return periodos.findFirstByEstado(ABIERTO);
    }

    Periodo abiertoOFalla() {
        return abierto().orElseThrow(() ->
                ApiException.noProcesable("SIN_PERIODO_ABIERTO", "No hay un periodo de matrícula abierto"));
    }

    private Periodo buscar(String codigo) {
        return periodos.findByCodigo(codigo)
                .orElseThrow(() -> ApiException.noEncontrado("PERIODO_NO_ENCONTRADO", "No existe el periodo " + codigo));
    }
}
