import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/** Escritura de resultados a un archivo de texto. */
public class output {

    /** Escribe outText en el archivo outFile (lo sobrescribe). */
    public void writeData(String outFile, String outText) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(outFile))) {
            writer.print(outText);
        } catch (IOException e) {
            System.err.println("Error al escribir " + outFile + ": " + e.getMessage());
        }
    }
}
