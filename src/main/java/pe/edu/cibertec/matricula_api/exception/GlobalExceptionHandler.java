package pe.edu.cibertec.matricula_api.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.TreeMap;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ApiException.class)
    ResponseEntity<ProblemDetail> handleApi(ApiException ex) {
        ResponseEntity.BodyBuilder res = ResponseEntity.status(ex.getStatus());
        if (ex.getStatus() == HttpStatus.UNAUTHORIZED) {
            res.header(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
        }
        return res.body(problema(ex.getStatus(), ex.getCodigo(), ex.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ProblemDetail> handleAuth(AuthenticationException ex) {
        ProblemDetail pd = ex instanceof OAuth2AuthenticationException
                ? problema(HttpStatus.UNAUTHORIZED, "TOKEN_INVALIDO", "El token es inválido, venció o su sesión fue cerrada")
                : problema(HttpStatus.UNAUTHORIZED, "NO_AUTENTICADO", "Debe iniciar sesión para usar este recurso");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header(HttpHeaders.WWW_AUTHENTICATE, "Bearer").body(pd);
    }

    @ExceptionHandler(AccessDeniedException.class)
    ProblemDetail handleDenied(AccessDeniedException ex) {
        return problema(HttpStatus.FORBIDDEN, "ACCESO_DENEGADO", "Su rol no tiene permiso para este recurso");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errores = new TreeMap<>();
        ex.getFieldErrors().forEach(e -> errores.put(e.getField(), e.getDefaultMessage()));

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, "Datos de entrada inválidos");
        pd.setProperty("codigo", "DATOS_INVALIDOS");
        pd.setProperty("errores", errores);
        return handleExceptionInternal(ex, pd, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        ResponseEntity<Object> res = super.handleExceptionInternal(ex, body, headers, status, request);
        if (res != null && res.getBody() instanceof ProblemDetail pd
                && (pd.getProperties() == null || !pd.getProperties().containsKey("codigo"))) {
            pd.setProperty("codigo", HttpStatus.valueOf(status.value()).name());
        }
        return res;
    }

    private static ProblemDetail problema(HttpStatus status, String codigo, String detalle) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalle);
        pd.setProperty("codigo", codigo);
        return pd;
    }
}
