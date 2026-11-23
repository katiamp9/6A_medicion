/**
 * Programa 6: encuentra el valor x tal que la integral de la función t
 * de 0 a x sea igual a p (búsqueda inversa).
 *
 * Algoritmo (PDF 6a, "Search algorithm"):
 *   1. x = 1.0 (valor de prueba inicial) y d = 0.5.
 *   2. Integrar de 0 a x con Simpson y comparar contra p.
 *   3. Si |resultado - p| < E, terminar: x es la respuesta.
 *   4. Resultado muy bajo  -> x = x + d.
 *      Resultado muy alto  -> x = x - d.
 *   5. Regla de ajuste de d: mientras el signo del error no cambie, d queda igual;
 *      cada vez que el signo del error cambia, d = d / 2.
 *   6. Repetir desde el paso 2.
 */
public class TSearch {

    private static final double INITIAL_X = 1.0;
    private static final double INITIAL_D = 0.5;
    private static final double SIMPSON_FACTOR = 0.01;      // E de Simpson = E de la búsqueda * factor
    private static final int MAX_ITERATIONS = 10000;      // protección contra bucles infinitos

    private final double dblP;
    private final int intDOF;
    private final double dblE;
    private int intIterations;

    /**
     * @param dblP   área buscada, 0 < p < 0.5 (la t es simétrica: de 0 a infinito vale 0.5)
     * @param intDOF grados de libertad, >= 1
     * @param dblE   error aceptable sobre p (por ejemplo 0.00001)
     */
    public TSearch(double dblP, int intDOF, double dblE) {
        if (!(dblP > 0.0 && dblP < 0.5)) {
            throw new IllegalArgumentException("p debe estar entre 0 y 0.5 (exclusivo): " + dblP);
        }
        if (intDOF < 1) {
            throw new IllegalArgumentException("dof debe ser >= 1: " + intDOF);
        }
        if (!(dblE > 0.0)) {
            throw new IllegalArgumentException("E debe ser > 0: " + dblE);
        }
        this.dblP = dblP;
        this.intDOF = intDOF;
        this.dblE = dblE;
    }

    /** Integral de la función t de 0 a x (Simpson, programa 5). */
    public double integrate(double dblX) {
        if (dblX == 0.0) {                      // caso x = 0: el área es 0
            return 0.0;
        }
        return new SimpsonIntegration(10, dblE * SIMPSON_FACTOR, intDOF, dblX).integrate();
    }

    /** Ejecuta la búsqueda y devuelve x. */
    public double findX() {
        double x = INITIAL_X;
        double d = INITIAL_D;
        int lastSign = 0;                       // 0 = aún no hay error previo
        intIterations = 0;

        while (intIterations < MAX_ITERATIONS) {
            intIterations++;
            double error = integrate(x) - dblP;

            if (Math.abs(error) < dblE) {       // resultado dentro del error aceptable
                return x;
            }

            int sign = (error < 0) ? -1 : 1;    // -1 = muy bajo, +1 = muy alto
            if (lastSign != 0 && sign != lastSign) {
                d = d / 2.0;                    // el signo cambió: se reduce d
            }
            lastSign = sign;

            if (sign < 0) {
                x = x + d;                      // muy bajo: aumentar x
            } else {
                while (x - d < 0.0) {           // evita x negativo (la t solo se integra para x >= 0)
                    d = d / 2.0;
                }
                x = x - d;                      // muy alto: disminuir x
            }
        }
        throw new IllegalStateException("No convergió en " + MAX_ITERATIONS + " iteraciones");
    }

    public int getIterations() { return intIterations; }
}
