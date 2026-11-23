import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TSearchTest {

    private static final double E = 0.0000001;
    private static final double X_TOL = 0.0001;   

    @Test
    @DisplayName("Tabla 1, prueba 1: p=0.20, dof=6 -> x=0.55338")
    void table1Test1() {
        assertEquals(0.55338, new TSearch(0.20, 6, E).findX(), X_TOL);
    }

    @Test
    @DisplayName("Tabla 1, prueba 2: p=0.45, dof=15 -> x=1.75305")
    void table1Test2() {
        assertEquals(1.75305, new TSearch(0.45, 15, E).findX(), X_TOL);
    }

    @Test
    @DisplayName("Tabla 1, prueba 3: p=0.495, dof=4 -> x=4.60409")
    void table1Test3() {
        assertEquals(4.60409, new TSearch(0.495, 4, E).findX(), X_TOL);
    }

    @Test
    @DisplayName("Ida y vuelta: integrar de 0 a x devuelve p (p=0.20, dof=6)")
    void roundTrip() {
        TSearch search = new TSearch(0.20, 6, E);
        assertEquals(0.20, search.integrate(search.findX()), E);
    }

    @Test
    @DisplayName("p muy pequeño: x cercano a 0 (p=0.001, dof=5 -> x aprox. 0.002634)")
    void verySmallP() {
        assertEquals(0.002634, new TSearch(0.001, 5, E).findX(), 0.00001);
    }

    @Test
    @DisplayName("Integrar de 0 a 0 vale 0")
    void integrateZero() {
        assertEquals(0.0, new TSearch(0.2, 6, E).integrate(0.0), 1e-12);
    }

    @Test
    @DisplayName("La búsqueda realiza al menos una iteración")
    void iterationsCounted() {
        TSearch search = new TSearch(0.20, 6, E);
        search.findX();
        assertTrue(search.getIterations() > 0);
    }

    @Test
    @DisplayName("p = 0 lanza excepción")
    void pZero() {
        assertThrows(IllegalArgumentException.class, () -> new TSearch(0.0, 6, E));
    }

    @Test
    @DisplayName("p = 0.5 lanza excepción")
    void pHalf() {
        assertThrows(IllegalArgumentException.class, () -> new TSearch(0.5, 6, E));
    }

    @Test
    @DisplayName("dof = 0 lanza excepción")
    void dofZero() {
        assertThrows(IllegalArgumentException.class, () -> new TSearch(0.2, 0, E));
    }

    @Test
    @DisplayName("E = 0 lanza excepción")
    void errorZero() {
        assertThrows(IllegalArgumentException.class, () -> new TSearch(0.2, 6, 0.0));
    }
}
