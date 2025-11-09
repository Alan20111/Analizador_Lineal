/**
 * @author TuNombre
 * @version 2.1 (Clase de Datos Pura)
 * * Clase para almacenar y operar matrices.
 * NO contiene lógica de resolución, solo datos.
 */
public class c_Matriz {

    // --- Atributos ---
    private double[][] a_data; // Los números
    private int a_filas;
    private int a_cols;

    /**
     * Constructor.
     */
    public c_Matriz(double[][] p_data) {
        this.a_filas = p_data.length;
        this.a_cols = p_data[0].length;
        // Copia profunda de los datos
        this.a_data = new double[a_filas][a_cols];
        for (int i = 0; i < a_filas; i++) {
            for (int j = 0; j < a_cols; j++) {
                this.a_data[i][j] = p_data[i][j];
            }
        }
    }

    /**
     * Muestra la matriz en consola.
     */
    public void m_print() {
        System.out.println("Matriz (" + a_filas + "x" + a_cols + "):");
        for (int i = 0; i < a_filas; i++) {
            System.out.print("[ ");
            for (int j = 0; j < a_cols; j++) {
                System.out.printf("%8.2f\t", a_data[i][j]);
            }
            System.out.println("]");
        }
    }

    // --- Getters (Para que el Solver pueda leer los datos) ---

    public int m_getFILAS() {
        return a_filas;
    }

    public int m_getCOLS() {
        return a_cols;
    }

    /**
     * Devuelve una COPIA de los datos para que el Solver trabaje.
     * @return un nuevo array double[][]
     */
    public double[][] m_getDATA() {
        // Devuelve una copia para proteger los datos originales
        double[][] v_copia = new double[a_filas][a_cols];
        for (int i = 0; i < a_filas; i++) {
            for (int j = 0; j < a_cols; j++) {
                v_copia[i][j] = this.a_data[i][j];
            }
        }
        return v_copia;
    }
}