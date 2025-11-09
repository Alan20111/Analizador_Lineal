import java.util.ArrayList;
import java.util.List;

/**
 * @author TuNombre
 * @version 1.0 (El Cerebro)
 * * Clase experta en resolver sistemas de ecuaciones
 * usando Eliminación de Gauss-Jordan.
 */
public class c_GaussSolver {

    // --- Atributos ---
    private double[][] a_data; // ¡Aquí vivirá la matriz a resolver!
    private int a_filas;
    private int a_cols;
    private String a_tipoSolucion;
    private List<String> a_soluciones;

    private static final double k_EPSILON = 1e-10;

    /**
     * Constructor. Toma la matriz que debe resolver.
     */
    public c_GaussSolver(c_Matriz p_matriz) {
        // Obtiene una COPIA de los datos de la matriz
        this.a_data = p_matriz.m_getDATA();
        this.a_filas = p_matriz.m_getFILAS();
        this.a_cols = p_matriz.m_getCOLS();
        this.a_soluciones = new ArrayList<>();
        this.a_tipoSolucion = "Sin resolver";
    }

    /**
     * ¡LA MAGIA! (Eliminación de Gauss-Jordan)
     * Resuelve la matriz que se le dio en el constructor.
     */
    public void m_resolver() {
        int v_pivotFila = 0;
        for (int v_col = 0; v_col < a_cols - 1; v_col++) {
            if (v_pivotFila >= a_filas) {
                break;
            }

            // 1. Encontrar el Pivote (Pivoteo Parcial)
            int v_maxFila = v_pivotFila;
            for (int i = v_pivotFila + 1; i < a_filas; i++) {
                if (Math.abs(a_data[i][v_col]) > Math.abs(a_data[v_maxFila][v_col])) {
                    v_maxFila = i;
                }
            }

            // 2. Intercambiar Filas
            double[] v_temp = a_data[v_pivotFila];
            a_data[v_pivotFila] = a_data[v_maxFila];
            a_data[v_maxFila] = v_temp;

            if (Math.abs(a_data[v_pivotFila][v_col]) <= k_EPSILON) {
                continue;
            }

            // 3. Normalizar Fila Pivote
            double v_pivotValor = a_data[v_pivotFila][v_col];
            for (int j = v_col; j < a_cols; j++) {
                a_data[v_pivotFila][j] /= v_pivotValor;
            }

            // 4. Eliminar otras filas
            for (int i = 0; i < a_filas; i++) {
                if (i != v_pivotFila) {
                    double v_factor = a_data[i][v_col];
                    for (int j = v_col; j < a_cols; j++) {
                        a_data[i][j] = a_data[i][j] - v_factor * a_data[v_pivotFila][j];
                    }
                }
            }

            v_pivotFila++;
        }

        // --- ¡Terminamos! Ahora interpretamos el resultado ---
        m_interpretarRREF();
    }

    /**
     * Interpreta la matriz en Forma Escalonada Reducida (RREF).
     */
    private void m_interpretarRREF() {
        int v_numVars = a_cols - 1;

        // 1. Buscar inconsistencias (No Solución)
        for (int i = 0; i < a_filas; i++) {
            boolean v_filaCeros = true;
            for (int j = 0; j < v_numVars; j++) {
                if (Math.abs(a_data[i][j]) > k_EPSILON) {
                    v_filaCeros = false;
                    break;
                }
            }
            if (v_filaCeros && Math.abs(a_data[i][v_numVars]) > k_EPSILON) {
                this.a_tipoSolucion = "❌ SIN Solución (Sistema Inconsistente)";
                this.a_soluciones.add("Se encontró una contradicción (ej: 0 = 1).");
                return;
            }
        }

        // 2. Contar pivotes
        int v_rank = 0;
        for (int i = 0; i < a_filas; i++) {
            boolean v_filaNoCero = false;
            for (int j = 0; j < a_cols; j++) {
                if (Math.abs(a_data[i][j]) > k_EPSILON) {
                    v_filaNoCero = true;
                    break;
                }
            }
            if (v_filaNoCero) {
                v_rank++;
            }
        }

        // 3. Comparar rango con número de variables
        if (v_rank < v_numVars) {
            this.a_tipoSolucion = "♾️ Infinitas Soluciones (Variables Libres)";
            this.a_soluciones.add("El número de variables (" + v_numVars + ") es mayor que el rango (" + v_rank + ").");
        } else {
            this.a_tipoSolucion = "✅ Solución Única";
            for (int i = 0; i < v_numVars; i++) {
                double v_resultado = a_data[i][v_numVars];
                this.a_soluciones.add("c" + (i+1) + " = " + String.format("%.2f", v_resultado));
            }
        }
    }

    /**
     * Muestra la matriz resuelta en consola.
     */
    public void m_printMatrizResuelta() {
        System.out.println("Matriz (" + a_filas + "x" + a_cols + "):");
        for (int i = 0; i < a_filas; i++) {
            System.out.print("[ ");
            for (int j = 0; j < a_cols; j++) {
                System.out.printf("%8.2f\t", a_data[i][j]);
            }
            System.out.println("]");
        }
    }

    /**
     * Muestra el resultado final interpretado.
     */
    public void m_printRESULTADO() {
        System.out.println("Tipo de Solución: " + this.a_tipoSolucion);
        for (String v_sol : this.a_soluciones) {
            System.out.println("   -> " + v_sol);
        }
    }
    /**
     * Devuelve el string del tipo de solución.
     * @return El string (ej: "✅ Solución Única")
     */
    public String m_getTIPO_SOLUCION() {
        return this.a_tipoSolucion;
    }
}