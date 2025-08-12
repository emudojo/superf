package com.mycompany.superf;

import javax.swing.*;

public class SuperF {

    // Indicador para saber si estamos en modo de edición de productos
    private static boolean editingProducts = false;

    public static void main(String[] args) {
        // Inicia el bucle del menú principal
        showMainMenu(null);
    }
    public static void showMainMenu() {
        showMainMenu(null);
    }
    /**
     * Método estático para mostrar el menú principal y manejar las selecciones del usuario.
     * Este método puede ser llamado desde otros archivos para mostrar el menú correctamente.
     * Continuará mostrando el menú después de cada operación hasta que el usuario elija salir.
     */
    public static void showMainMenu(JFrame frame) {
        int opt = 1;

        // Solo mostrar el menú si no estamos en modo de edición de productos
        if (frame != null && frame.isShowing()) {
            frame.dispose();
        }
        String menu = "*-*-*-* MENU DE SUPER F *-*-*-*";

        String[] options = {
                "Agregar Producto",
                "Mostrar Productos",
                "Buscar Producto",
                "Facturar Compra",
                "Mostrar Facturas",
                "Buscar Factura",
                "Generar productos y facturas",
                "Viernes Negro",
                "Salir"
        };

        int selectedOption = displayMenuOptions(menu, options);

        // Convertir el índice basado en 0 a basado en 1 para la sentencia switch
        opt = selectedOption + 1;

        // Manejar el caso cuando el usuario cierra el diálogo (selectedOption = -1)
        if (selectedOption == -1) {
            opt = 9; // Tratar como opción de salida
        }

        switch (opt) {
            case 1:
                Gestion.agregarProducto(null);
                break;
            case 2:
                // Establecer la bandera para indicar que estamos entrando en modo edición de productos
                Gestion.mostrarProductos();
                break;
            case 3:
                Gestion.buscarProducto(); 
                break;
            case 4:
                Gestion.facturarCompra();
                break;
            case 5:
                Gestion.mostrarFacturas();
                break;
            case 6:
                Gestion.buscarFacturas();
                break;
            case 7:
                Gestion.generarDatos();
                break;
            case 8:
                Gestion.viernesNegro();
                break;
            case 9:
                JOptionPane.showMessageDialog(null, "¡Gracias por su usar el sistema Super F!");
                break;
            default:
                JOptionPane.showMessageDialog(null, "Opcion no valida.");
        }

    }

    /**
     * Método estático público para mostrar opciones de menú usando JOptionPane.
     * Este método puede ser llamado desde otros archivos para mostrar menús personalizados.
     * Utiliza showInputDialog con una lista desplegable para asegurar una disposición de una sola columna.
     */
    public static int displayMenuOptions(String menu, String[] options) {
        Object selectedValue = JOptionPane.showInputDialog(
                null,
                menu,
                "Sistema Super F",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        // Buscar el índice de la opción seleccionada
        if (selectedValue == null) {
            return -1; // Usuario canceló
        }

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selectedValue)) {
                return i;
            }
        }

        return -1; // No debería llegar aquí
    }

    // Método para establecer la bandera de edición (se puede llamar desde la clase Gestion)
    public static void setEditingProducts(boolean editing) {
        editingProducts = editing;
    }
}
