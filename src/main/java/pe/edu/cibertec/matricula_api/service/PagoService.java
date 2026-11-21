package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Service;
import pe.edu.cibertec.matricula_api.dto.PagoResponse;
import pe.edu.cibertec.matricula_api.exception.ApiException;
import pe.edu.cibertec.matricula_api.model.MetodoPago;
import pe.edu.cibertec.matricula_api.model.Pago;
import pe.edu.cibertec.matricula_api.repository.PagoRepository;
import pe.edu.cibertec.matricula_api.security.UsuarioActual;

import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagos;
    private final UsuarioActual actual;

    public PagoService(PagoRepository pagos, UsuarioActual actual) {
        this.pagos = pagos;
        this.actual = actual;
    }

    public List<PagoResponse> listar(String alumno, MetodoPago metodo) {
        String filtro = actual.esAdmin() ? alumno : actual.username();
        return pagos.buscar(filtro, metodo).stream().map(PagoResponse::of).toList();
    }

    public PagoResponse obtener(Long id) {
        Pago p = pagos.findById(id)
                .orElseThrow(() -> ApiException.noEncontrado("PAGO_NO_ENCONTRADO", "No existe el pago " + id));
        actual.permitirAlumno(p.getMatricula().getAlumno().getCodigo());
        return PagoResponse.of(p);
    }
}
