import java.util.Scanner;

/**
 * @author TuNombre
 * @version 5.0 (Módulo Universal)
 * * Gestiona el menú principal y la navegación del usuario.
 */
public class c_Menu {

    private c_AnalizadorLineal a_analizador;

    public c_Menu() {
        this.a_analizador = new c_AnalizadorLineal();
        this.a_analizador.m_initSCAN();
    }

    public void m_mostrarMenu() {
        int v_opcion = 0;

        do {
            System.out.println("\n===== ANALIZADOR LINEAL =====");
            System.out.println("1. Combinación Lineal (Ax=b)");
            System.out.println("2. Dependencia Lineal (LI / LD)");
            System.out.println("3. Conjunto Generador (Span)");
            System.out.println("4. Verificar si es una Base");
            System.out.println("5. Analizador Universal (¡Reporte Completo!)");
            System.out.println("6. Salir");
            System.out.println("=============================");
            System.out.print("Elige una opción: ");

            try {
                v_opcion = Integer.parseInt(a_analizador.m_getSCAN().nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingresa solo un número.");
                v_opcion = 0;
            }

            switch (v_opcion) {
                case 1:
                    a_analizador.m_moduloCOMBINACION();
                    break;
                case 2:
                    a_analizador.m_moduloDEPENDENCIA();
                    break;
                case 3:
                    a_analizador.m_moduloGENERADOR();
                    break;
                case 4:
                    a_analizador.m_moduloBASE();
                    break;
                case 5:
                    // ¡Llamando al Módulo Maestro!
                    a_analizador.m_moduloUNIVERSAL();
                    break;
                case 6:
                    System.out.println("¡Hasta luego! Mantente bien despierto.");
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
                    break;
            }
        } while (v_opcion != 6);

        a_analizador.m_closeSCAN();
    }
}