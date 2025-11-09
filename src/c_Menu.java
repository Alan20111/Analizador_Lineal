import java.util.Scanner;

/**
 * @author TuNombre
 * @version 1.0
 * * Gestiona el menú principal y la navegación del usuario.
 */
public class c_Menu {

    // --- Atributos ---
    private Scanner a_scan;
    private c_AnalizadorLineal a_analizador; // Referencia al analizador

    /**
     * Constructor. Inicializa los objetos necesarios.
     */
    public c_Menu() {
        this.a_analizador = new c_AnalizadorLineal();
        this.a_analizador.m_initSCAN(); // Inicializamos el Scanner del analizador
    }

    /**
     * Muestra el menú principal y procesa la selección del usuario.
     */
    public void m_mostrarMenu() {
        int v_opcion = 0;

        do {
            System.out.println("\n===== ANALIZADOR LINEAL =====");
            System.out.println("1. Verificar Combinación Lineal");
            System.out.println("2. Verificar Dependencia/Independencia Lineal (Próximamente)");
            System.out.println("3. Verificar si un conjunto es Generador (Próximamente)");
            System.out.println("4. Verificar si un conjunto es una Base (Próximamente)");
            System.out.println("5. Salir");
            System.out.println("=============================");
            System.out.print("Elige una opción: ");

            try {
                v_opcion = Integer.parseInt(a_analizador.m_getSCAN().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa solo un número.");
                v_opcion = 0; // Resetea la opción para evitar un bucle infinito si hay error
            }

            switch (v_opcion) {
                    case 1:
                        a_analizador.m_moduloCOMBINACION();
                        break;
                    case 2:
                        // ¡Activado! Ya no está "Próximamente"
                        a_analizador.m_moduloDEPENDENCIA();
                        break;
                    case 3:
                        System.out.println("Función aún no implementada. ¡Vuelve pronto!");
                        break;
                    case 4:
                        System.out.println("Función aún no implementada. ¡Vuelve pronto!");
                        break;
                    case 5:
                        System.out.println("¡Hasta luego! Mantente bien despierto.");
                        break;
                    default:
                        System.out.println("Opción no válida. Intenta de nuevo.");
                        break;
            }
        } while (v_opcion != 5);

        // Cerramos el scanner al salir del programa
        a_analizador.m_closeSCAN();
    }
}