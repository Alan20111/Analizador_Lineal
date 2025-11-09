import java.util.ArrayList;
import java.util.List;

/**
 * @author TuNombre
 * @version 2.0 (Calcula Rango)
 * * Clase experta en resolver sistemas y calcular Rango.
 */
public class c_GaussSolver {

    // --- Atributos ---
    private double[][] a_data; // Matriz resuelta (RREF)
    private int a_filas;
    private int a_cols;
    private String a_tipoSolucion;
    private List<String> a_soluciones;
    private int a_rank; // ¡NUEVO! Para guardar el rango

    private static final double k_EPSILON = 1e-10;

    /**
     * Constructor. Toma la matriz que debe resolver.
     */
    public c_GaussSolver(c_Matriz p_matriz) {
        this.a_data = p_matriz.m_getDATA();
        this.a_filas = p_matriz.m_getFILAS();
        this.a_cols = p_matriz.m_getCOLS();
        this.a_soluciones = new ArrayList<>();
        this.a_tipoSolucion = "Sin resolver";
        this.a_rank = 0;
    }

    /**
     * ¡NUEVO! Calcula el Rango de la matriz (número de pivotes).
     * Esta es la lógica central de Gauss-Jordan.
     */
    public void m_calcularRANK() {
        // Copia de los datos para trabajar
        double[][] v_data = this.a_data;

        int v_pivotFila = 0;
        // Recorremos TODAS las columnas
        for (int v_col = 0; v_col < a_cols; v_col++) {

            if (v_pivotFila >= a_filas) {
                break; // No más filas para pivotes
            }

            // 1. Encontrar el Pivote
            int v_maxFila = v_pivotFila;
            for (int i = v_pivotFila + 1; i < a_filas; i++) {
                if (Math.abs(v_data[i][v_col]) > Math.abs(v_data[v_maxFila][v_col])) {
                    v_maxFila = i;
                }
            }

            // 2. Intercambiar Filas
            double[] v_temp = v_data[v_pivotFila];
            v_data[v_pivotFila] = v_data[v_maxFila];
            v_data[v_maxFila] = v_temp;

            // Si el pivote es (casi) cero, esta columna no tiene pivote.
            // La saltamos y seguimos con la siguiente columna.
            if (Math.abs(v_data[v_pivotFila][v_col]) <= k_EPSILON) {
                continue;
            }

            // 3. Normalizar Fila Pivote
            double v_pivotValor = v_data[v_pivotFila][v_col];
            for (int j = v_col; j < a_cols; j++) {
                v_data[v_pivotFila][j] /= v_pivotValor;
            }

            // 4. Eliminar otras filas
            for (int i = 0; i < a_filas; i++) {
                if (i != v_pivotFila) {
                    double v_factor = v_data[i][v_col];
                    for (int j = v_col; j < a_cols; j++) {
                        v_data[i][j] = v_data[i][j] - v_factor * v_data[v_pivotFila][j];
                    }
                }
            }

            v_pivotFila++; // Pasamos a la siguiente fila pivote
        }

        // Almacenar la matriz resuelta
        this.a_data = v_data;

        // Contar las filas no nulas (pivotes) para obtener el Rango
        int v_rank = 0;
        for (int i = 0; i < a_filas; i++) {
            boolean v_filaNoCero = false;
            for (int j = 0; j < a_cols; j++) {
                if (Math.abs(this.a_data[i][j]) > k_EPSILON) {
                    v_filaNoCero = true;
                    break;
                }
            }
            if (v_filaNoCero) {
                v_rank++;
            }
        }
        this.a_rank = v_rank; // ¡Guardamos el Rango!
    }

    /**
     * Resuelve un sistema Ax=b (Combinación Lineal).
     * ASUME que la última columna es el vector de resultados.
     */
    public void m_resolver() {
        // 1. Calcular RREF y Rango
        m_calcularRANK();

        // 2. Interpretar el resultado
        int v_numVars = a_cols - 1;

        // 2a. Buscar inconsistencias (No Solución)
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

        // 2b. Comparar rango con número de variables
        if (this.a_rank < v_numVars) {
            this.a_tipoSolucion = "♾️ Infinitas Soluciones (Variables Libres)";
            this.a_soluciones.add("El número de variables (" + v_numVars + ") es mayor que el rango (" + this.a_rank + ").");
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
     */
    public String m_getTIPO_SOLUCION() {
        return this.a_tipoSolucion;
    }

    /**
     * ¡NUEVO! Devuelve el rango calculado.
     */
    public int m_getRANK() {
        return this.a_rank;
    }
}