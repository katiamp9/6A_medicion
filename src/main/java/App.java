import java.util.Locale;

/** Sin argumentos: ejecuta las tablas. Con dos argumentos: java ... App <p> <dof> */
public class App {

    private static final double E = 0.0000001;

    public static void main(String[] args) {
        if (args.length == 0) {
            Logic logic = new Logic();
            logic.logic2a();
        } else if (args.length == 2) {
            try {
                double p = Double.parseDouble(args[0]);
                int dof = Integer.parseInt(args[1]);
                double x = new TSearch(p, dof, E).findX();
                System.out.println(String.format(Locale.US,
                        "p = %.5f dof = %d    x = %.5f", p, dof, x));
            } catch (NumberFormatException e) {
                System.out.println("p debe ser decimal con punto (ej. 0.45) y dof entero (ej. 15).");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());  
            }
        } else {
            System.out.println("Uso: java -cp target/classes javareverse.App <p> <dof>");
        }
    }
}