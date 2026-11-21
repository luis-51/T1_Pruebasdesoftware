package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Hooks {

    // El ultimo digito del codigo es D (en este examen D = 3).
    private static final String NOMBRE = "Luis Angel Sanchez Cueva";
    private static final String CODIGO = "i202506013";

    @BeforeAll
    public static void identificacionDelAlumno() {
        String fechaHora = ZonedDateTime.now(ZoneId.of("America/Lima"))
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.out.println("==================================================");
        System.out.println(" EXAMEN T1 - PRUEBAS DE SOFTWARE (CIBERTEC)");
        System.out.println(" Alumno : " + NOMBRE);
        System.out.println(" Codigo : " + CODIGO);
        System.out.println(" Fecha  : " + fechaHora);
        System.out.println("==================================================");
    }

    @Before
    public void beforeScenario() {
        OnStage.setTheStage(new OnlineCast());
    }
}
