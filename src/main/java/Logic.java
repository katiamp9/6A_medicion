import java.util.Locale;

/** Coordina la ejecución de las tres pruebas de la Tabla 1 del PDF 5a. */
public class Logic {

    private int intNumSeg = 10;
    private double dblE = 0.00001;
    private int intDOF;
    private double dblX;

    /**
     * Error aceptable sobre p en la búsqueda del programa 6. Es más estricto que 0.00001:
     * cerca de la cola de la t (p=0.495, dof=4) la curva es casi plana y un error de 1e-5
     * en p equivale a ~0.0025 en x, lo que impediría igualar los 5 decimales de la Tabla 1.
     */
    private static final double DBL_E_SEARCH = 0.0000001;

    /** Programa 6: ejecuta las pruebas de la Tabla 1 del PDF 6a (búsqueda de x dado p). */
    public void logic2a() {
        double[] ps = {0.20, 0.45, 0.495};
        int[] dofs = {6, 15, 4};
        double[] expected = {0.55338, 1.75305, 4.60409};

        StringBuilder text = new StringBuilder();
        text.append(String.format(Locale.US, "%-8s %-5s %-10s %-10s%n",
        "p", "dof", "x esperado", "x actual"));

        for (int t = 0; t < ps.length; t++) {
            TSearch search = new TSearch(ps[t], dofs[t], DBL_E_SEARCH);
            double x = search.findX();
            text.append(String.format(Locale.US, "%-8.3f %-5d %-10.5f %-10.5f%n",
        ps[t], dofs[t], expected[t], x));
        }

        System.out.print(text);
        new output().writeData("resultados6.txt", text.toString());
    }
}
