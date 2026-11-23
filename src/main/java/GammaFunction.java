/**
 * Función Gamma (Γ) para argumentos enteros y semi-enteros (n/2).
 *
 * Reglas (PDF 5a, páginas 6 y 7):
 *   Γ(x) = (x - 1) * Γ(x - 1)
 *   Γ(1) = 1
 *   Γ(1/2) = sqrt(pi)
 *   Para enteros: Γ(x) = (x - 1)!
 */
public class GammaFunction {

    /** Último valor calculado de Gamma. */
    private double gammaValue;

    /**
     * Gamma para un valor entero positivo: Γ(x) = (x - 1)!
     * Ejemplo: Γ(5) = 4! = 24.
     */
    public double computeIntGamma(int intValue) {
        if (intValue < 1) {
            throw new IllegalArgumentException("intValue debe ser >= 1: " + intValue);
        }
        double result = 1.0;                       // Γ(1) = 1
        for (int i = 2; i < intValue; i++) {       // (x-1)! = 1*2*...*(x-1)
            result *= i;
        }
        gammaValue = result;
        return gammaValue;
    }

    /**
     * Gamma para un valor decimal múltiplo de 0.5 (1/2, 3/2, 5/2, ...).
     * Se aplica Γ(x) = (x-1)Γ(x-1) hasta llegar a Γ(1/2) = sqrt(pi).
     * Los valores enteros se delegan en computeIntGamma.
     * Ejemplo: Γ(9/2) = 7/2 * 5/2 * 3/2 * 1/2 * sqrt(pi) = 11.63173.
     */
    public double computeDblGamma(double doubleValue) {
        if (doubleValue <= 0) {
            throw new IllegalArgumentException("doubleValue debe ser > 0: " + doubleValue);
        }
        if (doubleValue == Math.rint(doubleValue)) {       // entero
            return computeIntGamma((int) doubleValue);
        }
        if (Math.abs(doubleValue * 2 - Math.rint(doubleValue * 2)) > 1e-12) {
            throw new IllegalArgumentException("Solo se soportan múltiplos de 0.5: " + doubleValue);
        }
        double result = Math.sqrt(Math.PI);                // Γ(1/2)
        // Γ(x) = (x-1)·Γ(x-1): se acumulan los factores 1/2, 3/2, ..., (x-1)
        for (double k = 0.5; k < doubleValue - 0.5 + 1e-12; k += 1.0) {
            result *= k;
        }
        gammaValue = result;
        return gammaValue;
    }
}
