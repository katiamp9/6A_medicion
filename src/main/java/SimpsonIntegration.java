import java.util.Locale;

/**
 * Integración numérica con la Regla de Simpson de la función de densidad
 * t de Student, desde 0 hasta x.
 *
 * Regla de Simpson (PDF 5a, página 4):
 *   p = (W/3) * [ F(0) + sum_{i=1,3,5..}^{num_seg-1} 4F(iW)
 *                      + sum_{i=2,4,6..}^{num_seg-2} 2F(iW) + F(x) ]
 *   con W = x / num_seg  (num_seg par).
 *
 * Función t (PDF 5a, página 6):
 *   F(x) = [Γ((dof+1)/2) / ((dof·π)^(1/2) · Γ(dof/2))] · (1 + x²/dof)^(-(dof+1)/2)
 */
public class SimpsonIntegration {

    private static final int MAX_DOUBLINGS = 20;     // protección contra bucles infinitos

    private int intNumSeg;
    private double dblW;
    private double dblE;
    private int intDOF;
    private double dblX;
    private double[] dblTotXi;             // x_i = i·W
    private double[] dblFirstBaseTerms;    // 1 + x_i²/dof
    private double dblExponent;            // -(dof+1)/2
    private double dblCoeff;               // Γ((dof+1)/2) / ((dof·π)^(1/2) Γ(dof/2))
    private double[] dblFxi;               // F(x_i)
    private double[] dblFinalTerms;        // (W/3)·multiplicador·F(x_i)
    private double dblFinalValue;          // p

    private final GammaFunction gamma = new GammaFunction();

    public SimpsonIntegration(int intNumSeg, double dblE, int intDOF, double dblX) {
        this.intNumSeg = intNumSeg;
        this.dblE = dblE;
        this.intDOF = intDOF;
        this.dblX = dblX;
    }

    /** Paso 2: W = x / num_seg (ancho del segmento). */
    public void computeW(int intNumSeg, double dblX) {
        this.dblW = dblX / intNumSeg;
    }

    /** Paso 3: x_i = i·W para i = 0..num_seg. */
    public void computeXi(int intNumSeg) {
        dblTotXi = new double[intNumSeg + 1];
        for (int i = 0; i <= intNumSeg; i++) {
            dblTotXi[i] = i * dblW;
        }
    }

    /** Paso 4: base de la potencia, 1 + x_i²/dof, para cada x_i. */
    public void computeFirstBaseTerms(int intNumSeg, double[] dblTotXi, int intDOF) {
        dblFirstBaseTerms = new double[intNumSeg + 1];
        for (int i = 0; i <= intNumSeg; i++) {
            dblFirstBaseTerms[i] = 1.0 + (dblTotXi[i] * dblTotXi[i]) / intDOF;
        }
    }

    /** Paso 5: exponente = -(dof+1)/2. */
    public void computeExponent(int intDOF) {
        dblExponent = -(intDOF + 1) / 2.0;
    }

    /** Paso 6: coeficiente constante Γ((dof+1)/2) / ((dof·π)^(1/2)·Γ(dof/2)). */
    public void computeCoefficient(int intDOF) {
        double numerator = gammaOf(intDOF + 1);        // Γ((dof+1)/2)
        double denominator = Math.sqrt(intDOF * Math.PI) * gammaOf(intDOF);  // (dof·π)^(1/2)·Γ(dof/2)
        dblCoeff = numerator / denominator;
    }

    /** Paso 7: F(x_i) = coeficiente · base_i ^ exponente. */
    public void computeFxi(int intNumSeg) {
        dblFxi = new double[intNumSeg + 1];
        for (int i = 0; i <= intNumSeg; i++) {
            dblFxi[i] = dblCoeff * Math.pow(dblFirstBaseTerms[i], dblExponent);
        }
    }

    /**
     * Paso 8: términos de Simpson (W/3)·m_i·F(x_i), con multiplicador m_i:
     * 1 en los extremos (i=0, i=num_seg), 4 en i impar, 2 en i par interior.
     */
    public void computeFinalTerms(int intNumSeg) {
        dblFinalTerms = new double[intNumSeg + 1];
        for (int i = 0; i <= intNumSeg; i++) {
            int multiplier;
            if (i == 0 || i == intNumSeg) {
                multiplier = 1;
            } else if (i % 2 == 1) {
                multiplier = 4;
            } else {
                multiplier = 2;
            }
            dblFinalTerms[i] = (dblW / 3.0) * multiplier * dblFxi[i];
        }
    }

    /** Paso 9: p = suma de todos los términos finales. */
    public void computeFinalValue(int intNumSeg) {
        double sum = 0.0;
        for (int i = 0; i <= intNumSeg; i++) {
            sum += dblFinalTerms[i];
        }
        dblFinalValue = sum;
    }

    /**
     * Cálculo completo con un número de segmentos dado (pasos 2 a 9).
     */
    private double integrateOnce(int numSeg) {
        computeW(numSeg, dblX);
        computeXi(numSeg);
        computeFirstBaseTerms(numSeg, dblTotXi, intDOF);
        computeExponent(intDOF);
        computeCoefficient(intDOF);
        computeFxi(numSeg);
        computeFinalTerms(numSeg);
        computeFinalValue(numSeg);
        return dblFinalValue;
    }

    /**
     * Algoritmo iterativo (PDF 5a, pág. 4, pasos 5-6):
     * calcula con num_seg, duplica num_seg y recalcula hasta que la
     * diferencia entre dos resultados consecutivos sea menor que E.
     * El último resultado es la respuesta.
     */
    public double integrate() {
        // Programa 6: con x = 0 el ancho W sería 0; la integral de 0 a 0 vale 0.
        if (dblX == 0.0) {
            dblFinalValue = 0.0;
            return dblFinalValue;
        }
        int numSeg = intNumSeg;
        double previous = integrateOnce(numSeg);
        for (int k = 0; k < MAX_DOUBLINGS; k++) {
            numSeg = numSeg * 2;
            double current = integrateOnce(numSeg);
            if (Math.abs(current - previous) < dblE) {
                intNumSeg = numSeg;
                return current;
            }
            previous = current;
        }
        intNumSeg = numSeg;
        return dblFinalValue;
    }

    /** Γ(n/2): entero si n es par, semi-entero si n es impar. */
    private double gammaOf(int twiceValue) {
        return (twiceValue % 2 == 0)
                ? gamma.computeIntGamma(twiceValue / 2)
                : gamma.computeDblGamma(twiceValue / 2.0);
    }

    // ---- Getters ----
    public int getNumSeg() { return intNumSeg; }
    public double getW() { return dblW; }
    public double getCoefficient() { return dblCoeff; }
    public double getExponent() { return dblExponent; }
    public double[] getXi() { return dblTotXi; }
    public double[] getFxi() { return dblFxi; }
    public double getFinalValue() { return dblFinalValue; }
}
