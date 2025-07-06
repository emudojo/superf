package com.mycompany.superf;

import javax.swing.*;

public class SuperF {

    // Flag to indicate if we're in product editing mode
    private static boolean editingProducts = false;

    public static void main(String[] args) {
        // Start the main menu loop
        showMainMenu(null);
    }
    public static void showMainMenu() {
        showMainMenu(null);
    }
    /**
     * Static method to display the main menu and handle user selections.
     * This method can be called from other files to properly display the menu.
     * It will continue to show the menu after each operation until the user chooses to exit.
     */
    public static void showMainMenu(JFrame frame) {
        int opt = 1;

        // Only show the menu if we're not in product editing mode
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

        // Convert 0-based index to 1-based for a switch statement
        opt = selectedOption + 1;

        // Handle case when user closes dialog (selectedOption = -1)
        if (selectedOption == -1) {
            opt = 9; // Treat as an exit option
        }

        switch (opt) {
            case 1:
                Gestion.agregarProducto(null);
                break;
            case 2:
                // Set flag to indicate we're entering product editing mode
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
     * Public static method to display menu options using JOptionPane.
     * This method can be called from other files to display custom menus.
     * Uses showInputDialog with a dropdown to ensure single column layout.
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

        // Find the index of the selected option
        if (selectedValue == null) {
            return -1; // User cancelled
        }

        for (int i = 0; i < options.length; i++) {
            if (options[i].equals(selectedValue)) {
                return i;
            }
        }

        return -1; // Should not reach here
    }

    // Method to set the editing flag (can be called from Gestion class)
    public static void setEditingProducts(boolean editing) {
        editingProducts = editing;
    }
}
