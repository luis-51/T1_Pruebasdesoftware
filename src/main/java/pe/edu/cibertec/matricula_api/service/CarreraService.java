package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.cibertec.matricula_api.dto.CarreraRequest;
import pe.edu.cibertec.matricula_api.dto.CarreraResponse;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.Carrera;
import pe.edu.cibertec.matricula_api.repository.AlumnoRepository;
import pe.edu.cibertec.matricula_api.repository.CarreraRepository;
import pe.edu.cibertec.matricula_api.repository.CursoRepository;

import java.util.List;

@Service
public class CarreraService {

    private final CarreraRepository carreras;
    private final CursoRepository cursos;
    private final AlumnoRepository alumnos;

    public CarreraService(CarreraRepository carreras, CursoRepository cursos, AlumnoRepository alumnos) {
        this.carreras = carreras;
        this.cursos = cursos;
        this.alumnos = alumnos;
    }

    public List<CarreraResponse> listar() {
        return carreras.findAllByOrderByCodigo().stream().map(CarreraResponse::of).toList();
    }

    public CarreraResponse obtener(String codigo) {
        return CarreraResponse.of(buscar(codigo));
    }

    @Transactional
    public CarreraResponse crear(CarreraRequest req) {
        if (carreras.existsByCodigo(req.codigo())) {
            throw ApiException.conflicto("CARRERA_DUPLICADA", "Ya existe la carrera " + req.codigo());
        }
        return CarreraResponse.of(carreras.save(new Carrera(req.codigo(), req.nombre())));
    }

    @Transactional
    public CarreraResponse actualizar(String codigo, CarreraRequest req) {
        Carrera carrera = buscar(codigo);
        carrera.renombrar(req.nombre());
        return CarreraResponse.of(carrera);
    }

    @Transactional
    public void eliminar(String codigo) {
        Carrera carrera = buscar(codigo);
        if (cursos.existsByCarrera(carrera) || alumnos.existsByCarrera(carrera)) {
            throw ApiException.conflicto("CARRERA_EN_USO", "La carrera " + codigo + " tiene cursos o alumnos");
        }
        carreras.delete(carrera);
    }

    Carrera buscar(String codigo) {
        return carreras.findByCodigo(codigo)
                .orElseThrow(() -> ApiException.noEncontrado("CARRERA_NO_ENCONTRADA", "No existe la carrera " + codigo));
    }
}
