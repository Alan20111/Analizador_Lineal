import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author TuNombre
 * @version 5.1 (COMPLETO - Arreglo de Módulos Vacíos)
 * * Clase Coordinadora con Módulos 1, 2, 3, 4 y el 5 (Universal).
 */
public class c_AnalizadorLineal {

    // --- Atributos ---
    private Scanner a_scan;

    // Patrones Regex (INTELIGENTES)
    private Pattern a_patronMATRIZ = Pattern.compile("^(\\[[^\\]]+\\]\\s*)+$");
    // Esto define un "número con espacios" y una "lista de números"
    private String v_regexNUMS = "\\s*\\-?\\d+\\.?\\d*\\s*(,\\s*\\-?\\d+\\.?\\d*\\s*)*";
    private Pattern a_patronVECTOR = Pattern.compile("^([a-zA-Z0-9]+<" + v_regexNUMS + ">\\s*)+$");
    private Pattern a_patronVECTOR_SOLO_V = Pattern.compile("^[a-zA-Z0-9]+<" + v_regexNUMS + ">(\\s+[a-zA-Z0-9]+<" + v_regexNUMS + ">)*\\s*$");
    private Pattern a_patronPOLINOMIO = Pattern.compile("^([a-zA-Z0-9]+\\([^\\)]+\\)\\s*)+$");


    // --- Métodos del Scanner ---
    public void m_initSCAN() { this.a_scan = new Scanner(System.in); }
    public Scanner m_getSCAN() { return this.a_scan; }
    public void m_closeSCAN() { if (this.a_scan != null) this.a_scan.close(); }


    // --- Módulo 1: Combinación Lineal (Vectores) ---
    public void m_moduloCOMBINACION() {
        System.out.println("\n### Módulo 1: Análisis de Sistemas (Vectores) ###");
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
            if (v_linea.isEmpty()) { continue; }

            Matcher v_matchMATRIZ = a_patronMATRIZ.matcher(v_linea);
            Matcher v_matchVECTOR = a_patronVECTOR.matcher(v_linea);
            c_Matriz v_matriz = null;

            if (v_matchMATRIZ.matches()) {
                System.out.println("--- Formato Matriz Detectado ---");
                v_matriz = m_parsearMATRIZ(v_linea);
            }
            else if (v_matchVECTOR.matches()) {
                System.out.println("--- Formato Vector Detectado ---");
                v_matriz = m_parsearVECTORES_Ax_b(v_linea); // Parser Ax=b
            }
            else {
                System.out.println("[ERROR] ¡Formato no reconocido o mixto!");
                continue;
            }

            if (v_matriz == null) {
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
            } else {
                System.out.println("--- Matriz Aumentada Construida ---");
                v_matriz.m_print();
                c_GaussSolver v_solver = new c_GaussSolver(v_matriz);
                v_solver.m_resolver(); // Resuelve Ax=b
                System.out.println("\n--- Matriz Resuelta (Gauss-Jordan) ---");
                v_solver.m_printMatrizResuelta();
                v_solver.m_printRESULTADO();
                break;
            }
        }
    }

    // --- Módulo 2: Dependencia Lineal (LI/LD) ---
    public void m_moduloDEPENDENCIA() {
        System.out.println("\n### Módulo 2: Dependencia Lineal (LI / LD) ###");
        System.out.println("GUÍA DE USO: Introduce SOLO los vectores que quieres probar.");
        System.out.println("Formato: V1<x,y> V2<x,y> V3<x,y> ...");
        System.out.println("¡OJO! NO incluyas un vector 'W'. El programa asumirá W<0,0> automáticamente.");
        System.out.println("\nEscribe 'cancelar' para volver al menú.");
        System.out.println("-----------------------------------------------------");

        while (true) {
            System.out.print("\nIntroduce los vectores: ");
            String v_linea = a_scan.nextLine().trim();

            if (v_linea.equalsIgnoreCase("cancelar")) { break; }
            if (v_linea.isEmpty()) { continue; }

            String v_fullQuery = "";
            try {
                Matcher v_matchVECTOR = a_patronVECTOR_SOLO_V.matcher(v_linea);
                if (!v_matchVECTOR.matches()) {
                    System.out.println("[ERROR] Formato no reconocido. Asegúrate de usar 'Nombre<...>'");
                    System.out.println("Recuerda: NO escribas 'W'.");
                    continue;
                }

                Pattern v_patronDim = Pattern.compile("<([^>]+)>");
                Matcher v_matcherDim = v_patronDim.matcher(v_linea);

                if (v_matcherDim.find()) {
                    String[] v_nums = v_matcherDim.group(1).split(",");
                    String v_W_string = "W<0";
                    for (int i = 1; i < v_nums.length; i++) v_W_string += ",0";
                    v_W_string += "> ";
                    v_fullQuery = v_W_string + v_linea;
                } else { throw new Exception("No se pudo determinar la dimensión."); }
            } catch (Exception e) {
                System.out.println("[ERROR] No se pudo procesar la entrada: " + e.getMessage());
                continue;
            }

            c_Matriz v_matriz = m_parsearVECTORES_Ax_b(v_fullQuery);

            if (v_matriz == null) {
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
            } else {
                System.out.println("--- Matriz Aumentada (W=0) ---");
                v_matriz.m_print();

                c_GaussSolver v_solver = new c_GaussSolver(v_matriz);
                v_solver.m_resolver();

                System.out.println("\n--- Resultado del Análisis ---");
                String v_tipoSol = v_solver.m_getTIPO_SOLUCION();

                if (v_tipoSol.contains("Única")) {
                    System.out.println("✅ El sistema tiene SOLO la solución trivial (c1=0, c2=0...).");
                    System.out.println("Resultado: Los vectores son LINEALMENTE INDEPENDIENTES (LI).");
                } else if (v_tipoSol.contains("Infinitas")) {
                    System.out.println("♾️ El sistema tiene infinitas soluciones (variables libres).");
                    System.out.println("Resultado: Los vectores son LINEALMENTE DEPENDIENTES (LD).");
                }
                break;
            }
        }
    }

    // --- Módulo 3: Conjunto Generador (Span) ---
    public void m_moduloGENERADOR() {
        System.out.println("\n### Módulo 3: Conjunto Generador (Span) ###");
        System.out.println("Determina si los vectores dados generan el espacio R^n.");
        System.out.println("Formato: V1<x,y> V2<x,y> ...");
        System.out.println("¡OJO! NO incluyas un vector 'W'.");
        System.out.println("-----------------------------------------------------");

        while (true) {
            System.out.print("\nIntroduce los vectores: ");
            String v_linea = a_scan.nextLine().trim();

            if (v_linea.equalsIgnoreCase("cancelar")) { break; }
            if (v_linea.isEmpty()) { continue; }

            c_Matriz v_matriz_A = m_parsearVECTORES_MatrizA(v_linea);

            if (v_matriz_A == null) {
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
            } else {
                int v_dimension = v_matriz_A.m_getFILAS();
                System.out.println("--- Matriz de Vectores (A) Construida ---");
                v_matriz_A.m_print();

                c_GaussSolver v_solver = new c_GaussSolver(v_matriz_A);
                v_solver.m_calcularRANK();
                int v_rank = v_solver.m_getRANK();

                System.out.println("\n--- Resultado del Análisis ---");
                System.out.println("Dimensión del espacio (n): " + v_dimension);
                System.out.println("Rango de la matriz (Rank(A)): " + v_rank);

                if (v_rank == v_dimension) {
                    System.out.println("✅ ¡SÍ GENERAN!");
                    System.out.println("Como Rank(A) == Dimensión, los vectores generan R^" + v_dimension + ".");
                } else {
                    System.out.println("❌ NO GENERAN.");
                    System.out.println("Como Rank(A) < Dimensión, los vectores NO generan R^" + v_dimension + ".");
                }
                break;
            }
        }
    }

    // --- Módulo 4: Base ---
    public void m_moduloBASE() {
        System.out.println("\n### Módulo 4: Verificar si es una Base ###");
        System.out.println("Determina si los vectores son LI y Generan R^n.");
        System.out.println("Formato: V1<x,y> V2<x,y> ...");
        System.out.println("¡OJO! NO incluyas un vector 'W'.");
        System.out.println("-----------------------------------------------------");

        while (true) {
            System.out.print("\nIntroduce los vectores: ");
            String v_linea = a_scan.nextLine().trim();

            if (v_linea.equalsIgnoreCase("cancelar")) { break; }
            if (v_linea.isEmpty()) { continue; }

            c_Matriz v_matriz_A = m_parsearVECTORES_MatrizA(v_linea);

            if (v_matriz_A == null) {
                System.out.println("Por favor, vuelve a intentarlo o escribe 'cancelar'.");
            } else {
                int v_dimension = v_matriz_A.m_getFILAS();
                int v_numVectores = v_matriz_A.m_getCOLS();

                System.out.println("--- Matriz de Vectores (A) Construida ---");
                v_matriz_A.m_print();

                c_GaussSolver v_solver = new c_GaussSolver(v_matriz_A);
                v_solver.m_calcularRANK();
                int v_rank = v_solver.m_getRANK();

                System.out.println("\n--- Resultado del Análisis ---");
                System.out.println("Dimensión (n): " + v_dimension);
                System.out.println("Num. Vectores (k): " + v_numVectores);
                System.out.println("Rango (Rank(A)): " + v_rank);

                if (v_numVectores == v_dimension && v_rank == v_dimension) {
                    System.out.println("✅ ¡SÍ ES UNA BASE!");
                    System.out.println(" (Num. Vectores = Dimensión = Rango)");
                } else {
                    System.out.println("❌ NO ES UNA BASE.");
                    if (v_numVectores != v_dimension) {
                        System.out.println("Motivo: El número de vectores (" + v_numVectores + ") no es igual a la dimensión (" + v_dimension + ").");
                    } else {
                        System.out.println("Motivo: Los vectores son Linealmente Dependientes (Rango < Dimensión).");
                    }
                }
                break;
            }
        }
    }

    // --- Módulo 5: Analizador Universal ---
    public void m_moduloUNIVERSAL() {
        System.out.println("\n### Módulo 5: Analizador Universal (Modo Mago) ###");
        System.out.println("GUÍA DE USO: Introduce tu consulta. El programa detectará tu intención.");
        System.out.println("1. (Combinación Lineal): W<...> V1<...> o W(...) o [...]");
        System.out.println("2. (Análisis Completo): V1<...> V2<...>");
        System.out.println("\nEscribe 'cancelar' para volver al menú.");
        System.out.println("-----------------------------------------------------");

        while (true) {
            System.out.print("\nIntroduce tu consulta universal: ");
            String v_linea = a_scan.nextLine().trim();
            String v_lineaUPPER = v_linea.toUpperCase();

            if (v_linea.equalsIgnoreCase("cancelar")) { break; }
            if (v_linea.isEmpty()) { continue; }

            c_Matriz v_matriz = null;
            String v_tipoConsulta = "ERROR";

            // --- EL SUPER-DETECTOR ---
            if (v_lineaUPPER.startsWith("[")) {
                v_tipoConsulta = "COMBINACION_MATRIZ";
            } else if (v_lineaUPPER.startsWith("W(")) {
                v_tipoConsulta = "COMBINACION_POLI";
            } else if (v_lineaUPPER.startsWith("W<")) {
                v_tipoConsulta = "COMBINACION_VECTOR";
            } else if (a_patronVECTOR_SOLO_V.matcher(v_linea).matches()) {
                v_tipoConsulta = "ANALISIS_COMPLETO";
            } else {
                System.out.println("[ERROR] ¡Formato no reconocido!");
                System.out.println("Asegúrate de que la entrada sea válida.");
                continue;
            }

            // --- Procesar según el tipo de consulta ---
            try {
                // --- CASO 1: Es un problema de Combinación Lineal (Ax=b) ---
                if (v_tipoConsulta.startsWith("COMBINACION")) {
                    System.out.println("--- Detectada Consulta: Combinación Lineal (Ax=b) ---");

                    if (v_tipoConsulta.equals("COMBINACION_MATRIZ")) {
                        v_matriz = m_parsearMATRIZ(v_linea);
                    } else if (v_tipoConsulta.equals("COMBINACION_VECTOR")) {
                        v_matriz = m_parsearVECTORES_Ax_b(v_linea);
                    } else if (v_tipoConsulta.equals("COMBINACION_POLI")) {
                        v_matriz = m_parsearPOLINOMIOS_Ax_b(v_linea);
                    }

                    if (v_matriz == null) { throw new Exception("El parser falló."); }

                    System.out.println("--- Matriz Aumentada Construida ---");
                    v_matriz.m_print();
                    c_GaussSolver v_solver = new c_GaussSolver(v_matriz);
                    v_solver.m_resolver(); // Resuelve Ax=b

                    System.out.println("\n--- Matriz Resuelta (Gauss-Jordan) ---");
                    v_solver.m_printMatrizResuelta();
                    v_solver.m_printRESULTADO();
                }

                // --- CASO 2: Es un Análisis de Conjunto (LI/LD, Span, Base) ---
                else if (v_tipoConsulta.equals("ANALISIS_COMPLETO")) {
                    System.out.println("--- Detectada Consulta: Análisis Completo de Conjunto ---");

                    v_matriz = m_parsearVECTORES_MatrizA(v_linea);
                    if (v_matriz == null) { throw new Exception("El parser falló."); }

                    int v_dimension = v_matriz.m_getFILAS();
                    int v_numVectores = v_matriz.m_getCOLS();

                    System.out.println("--- Matriz de Vectores (A) Construida ---");
                    v_matriz.m_print();

                    c_GaussSolver v_solver = new c_GaussSolver(v_matriz);
                    v_solver.m_calcularRANK();
                    int v_rank = v_solver.m_getRANK();

                    // ¡El Reporte Completo que pediste!
                    System.out.println("\n======= REPORTE COMPLETO (BIEN DESPIERTO) =======");
                    System.out.println("1. Estadísticas del Conjunto:");
                    System.out.println("   -> Dimensión del Espacio (n): " + v_dimension);
                    System.out.println("   -> Número de Vectores (k): " + v_numVectores);
                    System.out.println("   -> Rango del Conjunto (Rank(A)): " + v_rank);

                    System.out.println("\n2. Análisis de Dependencia Lineal (Prueba LI/LD):");
                    if (v_rank == v_numVectores) {
                        System.out.println("   -> ✅ Son LINEALMENTE INDEPENDIENTES (LI). (Rango = Num. Vectores)");
                    } else {
                        System.out.println("   -> ♾️ Son LINEALMENTE DEPENDIENTES (LD). (Rango < Num. Vectores)");
                    }

                    System.out.println("\n3. Análisis de Conjunto Generador (Prueba Span):");
                    if (v_rank == v_dimension) {
                        System.out.println("   -> ✅ ¡SÍ GENERAN el espacio R^" + v_dimension + "! (Rango = Dimensión)");
                    } else {
                        System.out.println("   -> ❌ NO GENERAN el espacio R^" + v_dimension + ". (Rango < Dimensión)");
                    }

                    System.out.println("\n4. Conclusión: ¿Es una Base?");
                    if (v_rank == v_dimension && v_rank == v_numVectores) {
                        System.out.println("   -> ✅ ¡SÍ ES UNA BASE! (Rango = Dimensión = Num. Vectores)");
                    } else {
                        System.out.println("   -> ❌ NO ES UNA BASE.");
                    }
                    System.out.println("==================================================");
                }

                break; // Éxito, salir del bucle

            } catch (Exception e) {
                System.out.println("[ERROR] Ocurrió un error procesando la consulta.");
                if(v_matriz == null) System.out.println("El parser no pudo construir la matriz.");
                System.out.println("Detalle: " + e.getMessage());
            }
        }
    }


    // --- Métodos Parser (Privados) ---

    /**
     * Helper 1: "Pre-lee" la línea y encuentra el grado más alto.
     */
    private int m_encontrarMaxGrado(String p_linea) {
        int v_maxGrado = 0;
        Pattern v_patronGrado = Pattern.compile("x(\\^(\\d+))?");
        Matcher v_matcher = v_patronGrado.matcher(p_linea);

        while (v_matcher.find()) {
            int v_grado;
            if (v_matcher.group(2) != null) {
                v_grado = Integer.parseInt(v_matcher.group(2));
            } else {
                v_grado = 1;
            }
            if (v_grado > v_maxGrado) {
                v_maxGrado = v_grado;
            }
        }
        return v_maxGrado;
    }

    /**
     * Helper 2: Parsea un string de fórmula (ej: "3x^2+2x+9")
     * a un vector de coeficientes (ej: [3, 2, 9]).
     */
    private double[] m_parsearPOLINOMIO_aVector(String p_formula, int p_maxGrado) throws Exception {
        double[] v_vector = new double[p_maxGrado + 1];
        String v_formula = p_formula.replaceAll("\\s", "").replaceAll("\\-", "+-");
        String[] v_terminos = v_formula.split("\\+");

        for (String v_termino : v_terminos) {
            if (v_termino.isEmpty()) continue;

            double v_coef = 0;
            int v_grado = 0;

            if (v_termino.contains("x")) {
                String[] v_partesCoef = v_termino.split("x");

                if (v_partesCoef.length == 0 || v_partesCoef[0].isEmpty() || v_partesCoef[0].equals("+")) {
                    v_coef = 1;
                } else if (v_partesCoef[0].equals("-")) {
                    v_coef = -1;
                } else {
                    v_coef = Double.parseDouble(v_partesCoef[0]);
                }

                if (v_termino.contains("^")) {
                    v_grado = Integer.parseInt(v_termino.split("\\^")[1]);
                } else {
                    v_grado = 1;
                }

            }
            else {
                v_coef = Double.parseDouble(v_termino);
                v_grado = 0;
            }

            if (v_grado > p_maxGrado) {
                throw new Exception("Término '" + v_termino + "' tiene un grado mayor al máximo detectado.");
            }

            v_vector[p_maxGrado - v_grado] += v_coef;
        }
        return v_vector;
    }


    /**
     * Parser principal para el Módulo 5 (Polinomios Ax=b)
     */
    private c_Matriz m_parsearPOLINOMIOS_Ax_b(String p_linea) {
        try {
            List<String> v_nombresVISTOS = new ArrayList<>();
            List<double[]> v_vectoresDATA = new ArrayList<>();

            int v_maxGrado = m_encontrarMaxGrado(p_linea);
            int v_dimension = v_maxGrado + 1;
            System.out.println("--- Grado Máximo Detectado: " + v_maxGrado + " (Dimensión del Vector: " + v_dimension + ") ---");

            Pattern v_patron = Pattern.compile("([a-zA-Z0-9]+)\\(([^\\)]+)\\)");
            Matcher v_matcher = v_patron.matcher(p_linea.trim());

            while (v_matcher.find()) {
                String v_nombre = v_matcher.group(1);
                String v_formula = v_matcher.group(2);

                if (v_nombresVISTOS.contains(v_nombre)) {
                    System.out.println("[ERROR] ¡Nombre de polinomio duplicado! '" + v_nombre + "'");
                    return null;
                }
                v_nombresVISTOS.add(v_nombre);

                double[] v_vector = m_parsearPOLINOMIO_aVector(v_formula, v_maxGrado);
                v_vectoresDATA.add(v_vector);
            }

            if (v_vectoresDATA.size() < 2) {
                System.out.println("[ERROR] Se necesitan al menos 2 polinomios (Ej: W(...) y V1(...)).");
                return null;
            }

            if (!v_nombresVISTOS.get(0).equalsIgnoreCase("W")) {
                System.out.println("[ERROR] La consulta debe empezar con el polinomio objetivo 'W(...)'.");
                return null;
            }

            int v_numFilas = v_dimension;
            int v_numCols = v_vectoresDATA.size();
            double[][] v_data = new double[v_numFilas][v_numCols];
            double[] v_W = v_vectoresDATA.get(0);

            for (int j = 1; j < v_numCols; j++) {
                double[] v_V = v_vectoresDATA.get(j);
                for (int i = 0; i < v_numFilas; i++) v_data[i][j-1] = v_V[i];
            }
            for (int i = 0; i < v_numFilas; i++) v_data[i][v_numCols-1] = v_W[i];

            return new c_Matriz(v_data);

        } catch (Exception e) {
            System.out.println("[ERROR] Datos no operables en formato polinomio.");
            System.out.println("Detalle: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Parser para Módulo 1 y 2 (Sistema Ax=b)
    private c_Matriz m_parsearVECTORES_Ax_b(String p_linea) {
        try {
            List<String> v_nombresVISTOS = new ArrayList<>();
            List<double[]> v_vectoresDATA = new ArrayList<>();

            Pattern v_patron = Pattern.compile("([a-zA-Z0-9]+)<([^>]+)>");
            Matcher v_matcher = v_patron.matcher(p_linea.trim());

            int v_dimension = -1;
            boolean v_encontrado = false;

            while (v_matcher.find()) {
                v_encontrado = true;
                String v_nombre = v_matcher.group(1);
                String v_numsSTR = v_matcher.group(2);

                if (v_nombresVISTOS.contains(v_nombre)) {
                    System.out.println("[ERROR] ¡Nombre de vector duplicado! El nombre '" + v_nombre + "' se usa más de una vez.");
                    return null;
                }
                v_nombresVISTOS.add(v_nombre);

                String[] v_numsArray = v_numsSTR.replaceAll("\\s","").split(",");
                if (v_dimension == -1) v_dimension = v_numsArray.length;

                if (v_numsArray.length != v_dimension) {
                    System.out.println("[ERROR] ¡Vectores no coherentes!");
                    return null;
                }

                double[] v_vector = new double[v_dimension];
                for (int i = 0; i < v_dimension; i++) {
                    v_vector[i] = Double.parseDouble(v_numsArray[i]);
                }
                v_vectoresDATA.add(v_vector);
            }

            if (!v_encontrado || v_vectoresDATA.size() < 2) {
                System.out.println("[ERROR] Formato inválido o no hay suficientes vectores (se necesita W y al menos V1).");
                return null;
            }

            if (!v_nombresVISTOS.get(0).equalsIgnoreCase("W")) {
                System.out.println("[ERROR] ¡Formato Inválido para este módulo!");
                System.out.println("La consulta debe empezar con el vector objetivo 'W'.");
                System.out.println("Ejemplo: W<1,1> V1<2,2>");
                return null;
            }

            int v_numFilas = v_dimension;
            int v_numCols = v_vectoresDATA.size();
            double[][] v_data = new double[v_numFilas][v_numCols];
            double[] v_W = v_vectoresDATA.get(0);

            for (int j = 1; j < v_numCols; j++) {
                double[] v_V = v_vectoresDATA.get(j);
                for (int i = 0; i < v_numFilas; i++) v_data[i][j-1] = v_V[i];
            }
            for (int i = 0; i < v_numFilas; i++) v_data[i][v_numCols-1] = v_W[i];

            return new c_Matriz(v_data);

        } catch (Exception e) {
            System.out.println("[ERROR] Datos no operables en formato vector.");
            return null;
        }
    }

    // Parser para Módulos 3, 4 y 5 (Solo Matriz A)
    private c_Matriz m_parsearVECTORES_MatrizA(String p_linea) {
        try {
            List<String> v_nombresVISTOS = new ArrayList<>();
            List<double[]> v_vectoresDATA = new ArrayList<>();

            Matcher v_matchVECTOR = a_patronVECTOR_SOLO_V.matcher(p_linea);
            if (!v_matchVECTOR.matches()) {
                System.out.println("[ERROR] Formato no reconocido. Asegúrate de usar 'Nombre<...>'");
                return null;
            }

            Pattern v_patron = Pattern.compile("([a-zA-Z0-9]+)<([^>]+)>");
            Matcher v_matcher = v_patron.matcher(p_linea.trim());
            int v_dimension = -1;

            while (v_matcher.find()) {
                String v_nombre = v_matcher.group(1);
                String v_numsSTR = v_matcher.group(2);

                if (v_nombresVISTOS.contains(v_nombre)) {
                    System.out.println("[ERROR] ¡Nombre de vector duplicado! '" + v_nombre + "'");
                    return null;
                }
                v_nombresVISTOS.add(v_nombre);

                String[] v_numsArray = v_numsSTR.replaceAll("\\s","").split(",");
                if (v_dimension == -1) v_dimension = v_numsArray.length;

                if (v_numsArray.length != v_dimension) {
                    System.out.println("[ERROR] ¡Vectores no coherentes!");
                    return null;
                }

                double[] v_vector = new double[v_dimension];
                for (int i = 0; i < v_dimension; i++) {
                    v_vector[i] = Double.parseDouble(v_numsArray[i]);
                }
                v_vectoresDATA.add(v_vector);
            }

            if (v_vectoresDATA.isEmpty()) {
                System.out.println("[ERROR] No se encontraron vectores.");
                return null;
            }

            int v_numFilas = v_dimension;
            int v_numCols = v_vectoresDATA.size();
            double[][] v_data = new double[v_numFilas][v_numCols];

            for (int j = 0; j < v_numCols; j++) {
                double[] v_V = v_vectoresDATA.get(j);
                for (int i = 0; i < v_numFilas; i++) v_data[i][j] = v_V[i];
            }

            return new c_Matriz(v_data);

        } catch (Exception e) {
            System.out.println("[ERROR] Datos no operables en formato vector.");
            return null;
        }
    }

    // Parser para Módulo 1 y 5 (Formato Matriz)
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
                if (v_nums.length != v_numCols) {
                    System.out.println("[ERROR] ¡Matriz no coherente! Todas las filas deben tener el mismo número de elementos.");
                    return null;
                }
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