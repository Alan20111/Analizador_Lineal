import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TuNombre
 * @version 3.0 (Refactorizado con c_GaussSolver)
 * * Esta clase es el "Coordinador".
 * * Contiene la lógica del menú (detector) y los parsers.
 * * Delega la resolución a c_GaussSolver.
 */
public class c_AnalizadorLineal {

    // --- Atributos ---
    private Scanner a_scan;

    // Patrones Regex para el Detector Estricto
    private Pattern a_patronMATRIZ = Pattern.compile("^(\\[[^\\]]+\\]\\s*)+$");
    private Pattern a_patronVECTOR = Pattern.compile("^([a-zA-Z0-9]+<[^>]+>\\s*)+$");

    // --- Métodos del Scanner ---

    /**
     * Inicializa el objeto Scanner.
     */
    public void m_initSCAN() {
        this.a_scan = new Scanner(System.in);
    }

    /**
     * Permite a otras clases (como c_Menu) acceder al objeto Scanner.
     * @return El objeto Scanner inicializado.
     */
    public Scanner m_getSCAN() {
        return this.a_scan;
    }

    /**
     * Cierra el Scanner para liberar recursos.
     */
    public void m_closeSCAN() {
        if (this.a_scan != null) {
            this.a_scan.close();
        }
    }

    // --- Módulo Principal ---

    /**
     * Módulo 1: Bucle principal con DETECTOR ESTRICTO.
     * Detecta el formato, llama al parser, y le pasa la matriz al Solver.
     */
    public void m_moduloCOMBINACION() {
        System.out.println("\n### Módulo: Análisis de Sistemas (Gauss-Jordan) ###");
        System.out.println("GUÍA DE USO: Introduce tu consulta en CUALQUIER formato:");
        System.out.println("1. Formato Vector: W<w1,w2> V1<u1,u2> V2<v1,v2> ...");
        System.out.println("2. Formato Matriz: [f1_c1, f1_c2, ..., f1_res][f2_c1, ...] ...");
        System.out.println("\nEscribe 'cancelar' para volver al menú.");
        System.out.println("-----------------------------------------------------");

        while (true) {
            System.out.print("\nIntroduce tu consulta: ");
            String v_linea = a_scan.nextLine().trim();

            if (v_linea.equalsIgnoreCase("cancelar")) {
                System.out.println("Operación cancelada.");
                break;
            }

            if (v_linea.isEmpty()) {
                continue;
            }

            // --- EL DETECTOR ESTRICTO ---
            Matcher v_matchMATRIZ = a_patronMATRIZ.matcher(v_linea);
            Matcher v_matchVECTOR = a_patronVECTOR.matcher(v_linea);

            c_Matriz v_matriz = null;

            if (v_matchMATRIZ.matches()) {
                System.out.println("--- Formato Matriz Detectado ---");
                v_matriz = m_parsearMATRIZ(v_linea);
            }
            else if (v_matchVECTOR.matches()) {
                System.out.println("--- Formato Vector Detectado ---");
                v_matriz = m_parsearVECTORES(v_linea);
            }
            else {
                System.out.println("[ERROR] ¡Formato no reconocido o mixto!");
                System.out.println("Asegúrate de usar SOLO el formato de matriz [...] o SOLO el formato de vector Nombre<...>, no ambos.");
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
                continue; // Vuelve al inicio del while
            }

            // --- VALIDACIÓN Y EJECUCIÓN (Lógica de Refactor) ---
            if (v_matriz == null) {
                // El parser falló (ej. matriz no coherente), ya imprimió su error.
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
            } else {
                // ¡Éxito! Tenemos una matriz coherente.
                System.out.println("--- Matriz Aumentada Construida ---");
                v_matriz.m_print(); // Imprime la matriz original

                // --- Lógica de Solución (Refactorizada) ---
                // 1. Creamos el Solver y le pasamos la matriz
                c_GaussSolver v_solver = new c_GaussSolver(v_matriz);

                // 2. Le pedimos que resuelva
                v_solver.m_resolver();

                // 3. Le pedimos que imprima los resultados
                System.out.println("\n--- Matriz Resuelta (Gauss-Jordan) ---");
                v_solver.m_printMatrizResuelta();

                v_solver.m_printRESULTADO();
                // --- Fin de la Lógica de Solución ---

                break; // Sale del bucle
            }
        }
    }
    /**
     * Módulo 2: Bucle principal para Dependencia / Independencia Lineal.
     * Reutiliza el parser de vectores pero fuerza un vector W<0,0...>
     */
    /**
     * Módulo 2 (v2.0 - INTELIGENTE)
     * Revisa LI/LD y también Generadores/Base si el número
     * de vectores es igual a la dimensión del espacio.
     */
    public void m_moduloDEPENDENCIA() {
        System.out.println("\n### Módulo: Análisis de Dependencia, Generadores y Base ###");
        System.out.println("GUÍA DE USO: Introduce los vectores que quieres probar.");
        System.out.println("Formato: V1<x,y> V2<x,y> V3<x,y> ...");
        System.out.println("Ejemplo: V1<1,0> V2<0,1>");
        System.out.println("\nEscribe 'cancelar' para volver al menú.");
        System.out.println("-----------------------------------------------------");

        while (true) {
            System.out.print("\nIntroduce los vectores: ");
            String v_linea = a_scan.nextLine().trim();

            if (v_linea.equalsIgnoreCase("cancelar")) {
                System.out.println("Operación cancelada.");
                break;
            }
            if (v_linea.isEmpty()) {
                continue;
            }

            // --- Construir la consulta del sistema homogéneo ---
            String v_W_string = "";
            String v_fullQuery = "";
            int v_dimension = -1;

            try {
                Matcher v_matchVECTOR = a_patronVECTOR.matcher(v_linea);
                if (!v_matchVECTOR.matches()) {
                    System.out.println("[ERROR] Formato no reconocido. Asegúrate de usar 'Nombre<...>'");
                    continue;
                }

                Pattern v_patronDim = Pattern.compile("<([^>]+)>");
                Matcher v_matcherDim = v_patronDim.matcher(v_linea);

                if (v_matcherDim.find()) {
                    String[] v_nums = v_matcherDim.group(1).split(",");
                    v_dimension = v_nums.length; // Guardamos la dimensión

                    // Construir el vector W<0,0,...>
                    v_W_string = "W<0";
                    for (int i = 1; i < v_dimension; i++) {
                        v_W_string += ",0";
                    }
                    v_W_string += "> ";
                } else {
                    throw new Exception("No se pudo determinar la dimensión.");
                }

                v_fullQuery = v_W_string + v_linea;
                System.out.println("--- Sistema Homogéneo (W=0) construido ---");

            } catch (Exception e) {
                System.out.println("[ERROR] No se pudo procesar la entrada: " + e.getMessage());
                continue;
            }

            // --- Parsear y Resolver ---
            c_Matriz v_matriz = m_parsearVECTORES(v_fullQuery);

            if (v_matriz == null) {
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
            } else {
                System.out.println("--- Matriz Aumentada (W=0) ---");
                v_matriz.m_print();

                c_GaussSolver v_solver = new c_GaussSolver(v_matriz);
                v_solver.m_resolver();

                // --- INTERPRETACIÓN ---
                System.out.println("\n--- Resultado del Análisis ---");
                String v_tipoSol = v_solver.m_getTIPO_SOLUCION();
                boolean v_esLI = false;

                if (v_tipoSol.contains("Única")) {
                    System.out.println("✅ El sistema tiene SOLO la solución trivial (c1=0, c2=0...).");
                    System.out.println("Resultado: Los vectores son LINEALMENTE INDEPENDIENTES (LI).");
                    v_esLI = true;
                } else if (v_tipoSol.contains("Infinitas")) {
                    System.out.println("♾️ El sistema tiene infinitas soluciones (variables libres).");
                    System.out.println("Resultado: Los vectores son LINEALMENTE DEPENDIENTES (LD).");
                    v_esLI = false;
                }

                // --- ¡AQUÍ ESTÁ LO QUE FALTABA! ---
                // (v_matriz.m_getCOLS() - 1) es el número de vectores base (V1, V2...)
                int v_numVectoresBase = v_matriz.m_getCOLS() - 1;
                // v_dimension la calculamos arriba

                if (v_dimension == v_numVectoresBase) {
                    System.out.println("\n--- Análisis Adicional (Vectores = Dimensión) ---");
                    System.out.println("💡 (Detectados " + v_numVectoresBase + " vectores en un espacio de dimensión " + v_dimension + ")");

                    if (v_esLI) {
                        System.out.println("💡 ¡Bien despierto! Por el Teorema Fundamental, esto también implica:");
                        System.out.println("   -> GENERAN el espacio R^" + v_dimension + ".");
                        System.out.println("   -> Forman una BASE para R^" + v_dimension + ".");
                    } else { // Son LD
                        System.out.println("💡 (Como son LD, NO generan el espacio y NO son una base).");
                    }
                }

                break; // Éxito, salir del bucle
            }
        }
    }
    // --- Métodos Parser (Privados) ---

    /**
     * Parser para el Formato Vector: W<...> V1<...> V2<...>
     * Valida nombres duplicados y dimensiones.
     * Construye la matriz aumentada automáticamente.
     */
    private c_Matriz m_parsearVECTORES(String p_linea) {
        try {
            List<String> v_nombresVISTOS = new ArrayList<>();
            List<double[]> v_vectoresDATA = new ArrayList<>();

            Pattern v_patron = Pattern.compile("([a-zA-Z0-9]+)<([^>]+)>");
            Matcher v_matcher = v_patron.matcher(p_linea);

            int v_dimension = -1;
            boolean v_encontrado = false;

            while (v_matcher.find()) {
                v_encontrado = true;
                String v_nombre = v_matcher.group(1);
                String v_numsSTR = v_matcher.group(2);

                // 1. Validar Nombres Duplicados
                if (v_nombresVISTOS.contains(v_nombre)) {
                    System.out.println("[ERROR] ¡Nombre de vector duplicado! El nombre '" + v_nombre + "' se usa más de una vez.");
                    return null;
                }
                v_nombresVISTOS.add(v_nombre);

                // 2. Validar Dimensiones Coherentes
                String[] v_numsArray = v_numsSTR.split(",");
                if (v_dimension == -1) {
                    v_dimension = v_numsArray.length; // Fija la dimensión
                }

                if (v_numsArray.length != v_dimension) {
                    System.out.println("[ERROR] ¡Vectores no coherentes! Se esperaba dimensión " + v_dimension + " pero '" + v_nombre + "' tiene " + v_numsArray.length + " elementos.");
                    return null;
                }

                // 3. Parsear números
                double[] v_vector = new double[v_dimension];
                for (int i = 0; i < v_dimension; i++) {
                    v_vector[i] = Double.parseDouble(v_numsArray[i].trim());
                }
                v_vectoresDATA.add(v_vector);
            }

            if (!v_encontrado) {
                System.out.println("[ERROR] Formato de vector no reconocido. No se encontró patrón 'Nombre<nums>'.");
                return null;
            }

            if (v_vectoresDATA.size() < 2) {
                System.out.println("[ERROR] Se necesitan al menos 2 vectores (Ej: W<...> y V1<...>).");
                return null;
            }

            // --- Transponer Vectores a Matriz Aumentada ---
            int v_numFilas = v_dimension;
            int v_numCols = v_vectoresDATA.size();
            double[][] v_data = new double[v_numFilas][v_numCols];

            double[] v_W = v_vectoresDATA.get(0); // Vector W (objetivo) es el primero

            // Vectores Base (V1, V2...) se vuelven columnas
            for (int j = 1; j < v_numCols; j++) {
                double[] v_V = v_vectoresDATA.get(j);
                for (int i = 0; i < v_numFilas; i++) {
                    v_data[i][j-1] = v_V[i];
                }
            }
            // Vector W se vuelve la última columna (resultados)
            for (int i = 0; i < v_numFilas; i++) {
                v_data[i][v_numCols-1] = v_W[i];
            }

            return new c_Matriz(v_data);

        } catch (Exception e) {
            System.out.println("[ERROR] Datos no operables en formato vector. Revisa tus números.");
            return null;
        }
    }

    /**
     * Parser para el Formato Matriz: [...] [...]
     * Valida que todas las filas tengan el mismo número de elementos.
     */
    private c_Matriz m_parsearMATRIZ(String p_linea) {
        try {
            String v_lineaLimpia = p_linea.replace(" ", "");
            String[] v_filasSTR = v_lineaLimpia.split("\\]\\[");

            v_filasSTR[0] = v_filasSTR[0].substring(1);
            int v_numFilas = v_filasSTR.length;
            v_filasSTR[v_numFilas - 1] = v_filasSTR[v_numFilas - 1].substring(0, v_filasSTR[v_numFilas - 1].length() - 1);

            int v_numCols = v_filasSTR[0].split(",").length;
            double[][] v_data = new double[v_numFilas][v_numCols];

            for (int i = 0; i < v_numFilas; i++) {
                String[] v_nums = v_filasSTR[i].split(",");
                // 1. Validar Coherencia de Filas
                if (v_nums.length != v_numCols) {
                    System.out.println("[ERROR] ¡Matriz no coherente! Todas las filas deben tener el mismo número de elementos.");
                    return null;
                }
                // 2. Parsear números
                for (int j = 0; j < v_numCols; j++) {
                    v_data[i][j] = Double.parseDouble(v_nums[j]);
                }
            }
            return new c_Matriz(v_data);
        } catch (Exception e) {
            System.out.println("[ERROR] Datos no operables en formato matriz. Revisa tus números y comas.");
            return null;
        }
    }
}