package pe.edu.cibertec.matricula_api.service;

import org.springframework.stereotype.Component;

@Component
public class CalculadoraMatricula {

    public static final double PRECIO_CREDITO = 120.0;
    public static final int MAX_CREDITOS = 24;

    public double subtotal(int creditos) {
        return creditos * PRECIO_CREDITO;
    }

    public double descuento(double promedio) {
        if (promedio >= 17) return 0.20;
        if (promedio >= 15) return 0.10;
        return 0.0;
    }

    public double monto(int creditos, double promedio) {
        double subtotal = subtotal(creditos);
        return redondear(subtotal - subtotal * descuento(promedio));
    }

    public static double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }
}
