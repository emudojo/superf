package com.mycompany.superf;

import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Dimension;

public class Gestion {

    private static Producto productos[] = new Producto[40];
    private static Factura facturas[] = new Factura[25];
    private static int cantProductos = 0;
    private static int cantfacturas = 0;
    private static boolean generados = false;
    private static int descuento = 0;

    // Referencia estática al marco (frame) de productos para evitar crear múltiples instancias
    private static JFrame productosFrame = null;

    // Referencia estática al marco (frame) de facturas para evitar crear múltiples instancias
    private static JFrame facturasFrame = null;

    // METODOS DEL MENU:
    public static void agregarProducto(JFrame frame) {

            if (cantProductos < productos.length) {
                String codigo = JOptionPane.showInputDialog("Ingresar el codigo del producto:");

                if (buscarIndiceCodigo(codigo) == -1) { // Se puede agregar
                    String nombre = JOptionPane.showInputDialog("Ingrese el nombre del producto:");
                    String descripcion = JOptionPane.showInputDialog("Ingrese la descripcion [" + nombre + "]:");
                    double precio = Double.parseDouble(JOptionPane.showInputDialog("Ingrese el precio [" + nombre + "]:"));
                    int stock = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad [" + nombre + "]:"));

                    Producto nuevoProducto = new Producto(codigo, nombre, descripcion, precio, stock);

                    productos[cantProductos] = nuevoProducto;
                    cantProductos++;

                    JOptionPane.showMessageDialog(null, "Producto Agregado con exito:\n" + nuevoProducto.getDatos(true));
                } else { // No se puede agregar
                    int btn = mostrarBotones("¡El codigo [" + codigo + "] ya esta en el sistema!"
                            + "\n¿Que quieres hacer?", "Aviso",
                            JOptionPane.QUESTION_MESSAGE, new String[]{"Ingresar otro codigo", "Cancelar"});
                    if (btn != 0) {
                        return;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "La maxima cantidad de productos que se puede"
                        + " gestionar es de: " + productos.length + ".");
            }
            SuperF.showMainMenu(frame);
    }

    public static void mostrarProductos() {
        if (cantProductos == 0) {
            JOptionPane.showMessageDialog(null, "No hay productos en el sistema.");
            return;
        }

        // Set the editing flag to true when showing products
        SuperF.setEditingProducts(true);

        // Check if the frame already exists and is visible
        if (productosFrame != null && productosFrame.isDisplayable()) {
            // Refresh the existing frame content
            refreshProductosFrame();
            productosFrame.toFront();
            productosFrame.requestFocus();
            return;
        }

        // Create a JFrame to hold the grid and buttons
        productosFrame = new JFrame("Lista de Productos");
        productosFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        productosFrame.setSize(800, 500);
        productosFrame.setLayout(new BorderLayout());

        // Add window listener to reset editing flag when window is closed
        productosFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                SuperF.setEditingProducts(false);
                productosFrame = null; // Clear the reference when window is closed
            }
        });

        // Create a table model with column names
        String[] columnNames = {"Código", "Nombre", "Descripción", "Precio", "Stock", "Acciones"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the actions column is editable
            }
        };

        // Add products to the table
        for (int i = 0; i < cantProductos; i++) {
            Producto p = productos[i];
            model.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                "¢" + p.getPrecio(),
                p.getStock(),
                "Acciones"
            });
        }

        // Create the table and add it to a scroll pane
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        productosFrame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for the action buttons at the bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        // Add button
        JButton addButton = new JButton("Agregar Producto");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuperF.setEditingProducts(false); // Reset the editing flag
                productosFrame.dispose();
                productosFrame = null;
                agregarProducto(productosFrame);
                mostrarProductos(); // Refresh the grid after adding
            }
        });
        buttonPanel.add(addButton);

        // Modify button
        JButton modifyButton = new JButton("Modificar Producto");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigo = (String) model.getValueAt(selectedRow, 0);
                    SuperF.setEditingProducts(false); // Reset the editing flag
                    productosFrame.dispose();
                    productosFrame = null;
                    modificarProducto(codigo);
                    mostrarProductos(); // Refresh the grid after modifying
                } else {
                    JOptionPane.showMessageDialog(productosFrame, "Por favor, seleccione un producto para modificar.");
                }
            }
        });
        buttonPanel.add(modifyButton);

        // Delete button
        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigo = (String) model.getValueAt(selectedRow, 0);
                    int indice = buscarIndiceCodigo(codigo);

                    if (indice != -1) {
                        boolean sePuedeEliminar = true;
                        for (int i = 0; i < cantfacturas; i++) {
                            if (facturas[i].hayProductoFacturado(productos[indice])) {
                                sePuedeEliminar = false;
                                break;
                            }
                        }

                        if (sePuedeEliminar) {
                            int btn = mostrarBotones("¿Seguro que quieres eliminar el siguiente producto?\n" + 
                                    productos[indice].getDatos(true), "Eliminar", 
                                    JOptionPane.QUESTION_MESSAGE, new String[]{"SI", "NO"});

                            if (btn == 0) { // SI
                                for (int i = indice; i < cantProductos - 1; i++) {
                                    productos[i] = productos[i + 1];
                                }

                                productos[cantProductos - 1] = null;
                                cantProductos--;

                                JOptionPane.showMessageDialog(productosFrame, "El producto se eliminó con éxito.");
                                SuperF.setEditingProducts(false); // Reset the editing flag
                                productosFrame.dispose();
                                productosFrame = null;
                                mostrarProductos(); // Refresh the grid after deleting
                            }
                        } else {
                            JOptionPane.showMessageDialog(productosFrame, "El producto no se puede eliminar ya que se facturó.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(productosFrame, "Por favor, seleccione un producto para eliminar.");
                }
            }
        });
        buttonPanel.add(deleteButton);

        // Back to Main Menu button
        JButton backButton = new JButton("Volver al Menú Principal");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {;
                SuperF.showMainMenu(productosFrame);
            }
        });
        buttonPanel.add(backButton);

        // Add the button panel to the frame
        productosFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Código
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(250); // Descripción
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Precio
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stock
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Acciones

        // Display the frame
        productosFrame.setLocationRelativeTo(null);
        productosFrame.setVisible(true);
    }

    // Method to refresh the existing productos frame instead of creating a new one
    private static void refreshProductosFrame() {
        if (productosFrame == null) {
            return;
        }

        // Clear the existing content
        productosFrame.getContentPane().removeAll();
        productosFrame.setLayout(new BorderLayout());

        // Create a table model with column names
        String[] columnNames = {"Código", "Nombre", "Descripción", "Precio", "Stock", "Acciones"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only the actions column is editable
            }
        };

        // Add products to the table
        for (int i = 0; i < cantProductos; i++) {
            Producto p = productos[i];
            model.addRow(new Object[]{
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                "¢" + p.getPrecio(),
                p.getStock(),
                "Acciones"
            });
        }

        // Create the table and add it to a scroll pane
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        productosFrame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for the action buttons at the bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 0));

        // Add button
        JButton addButton = new JButton("Agregar Producto");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuperF.setEditingProducts(false); // Reset the editing flag
                productosFrame.dispose();
                productosFrame = null;
                agregarProducto(productosFrame);
                mostrarProductos(); // Refresh the grid after adding
            }
        });
        buttonPanel.add(addButton);

        // Modify button
        JButton modifyButton = new JButton("Modificar Producto");
        modifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigo = (String) model.getValueAt(selectedRow, 0);
                    SuperF.setEditingProducts(false); // Reset the editing flag
                    productosFrame.dispose();
                    productosFrame = null;
                    modificarProducto(codigo);
                    mostrarProductos(); // Refresh the grid after modifying
                } else {
                    JOptionPane.showMessageDialog(productosFrame, "Por favor, seleccione un producto para modificar.");
                }
            }
        });
        buttonPanel.add(modifyButton);

        // Delete button
        JButton deleteButton = new JButton("Eliminar Producto");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    String codigo = (String) model.getValueAt(selectedRow, 0);
                    int indice = buscarIndiceCodigo(codigo);

                    if (indice != -1) {
                        boolean sePuedeEliminar = true;
                        for (int i = 0; i < cantfacturas; i++) {
                            if (facturas[i].hayProductoFacturado(productos[indice])) {
                                sePuedeEliminar = false;
                                break;
                            }
                        }

                        if (sePuedeEliminar) {
                            int btn = mostrarBotones("¿Seguro que quieres eliminar el siguiente producto?\n" + 
                                    productos[indice].getDatos(true), "Eliminar", 
                                    JOptionPane.QUESTION_MESSAGE, new String[]{"SI", "NO"});

                            if (btn == 0) { // SI
                                for (int i = indice; i < cantProductos - 1; i++) {
                                    productos[i] = productos[i + 1];
                                }

                                productos[cantProductos - 1] = null;
                                cantProductos--;

                                JOptionPane.showMessageDialog(productosFrame, "El producto se eliminó con éxito.");
                                SuperF.setEditingProducts(false); // Reset the editing flag
                                productosFrame.dispose();
                                productosFrame = null;
                                mostrarProductos(); // Refresh the grid after deleting
                            }
                        } else {
                            JOptionPane.showMessageDialog(productosFrame, "El producto no se puede eliminar ya que se facturó.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(productosFrame, "Por favor, seleccione un producto para eliminar.");
                }
            }
        });
        buttonPanel.add(deleteButton);

        // Back to Main Menu button
        JButton backButton = new JButton("Volver al Menú Principal");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {;
                SuperF.showMainMenu(productosFrame);
            }
        });
        buttonPanel.add(backButton);

        // Add the button panel to the frame
        productosFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Código
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Nombre
        table.getColumnModel().getColumn(2).setPreferredWidth(250); // Descripción
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Precio
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Stock
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // Acciones

        // Refresh the frame
        productosFrame.revalidate();
        productosFrame.repaint();
    }

    public static void buscarProducto() {
        if (cantProductos == 0) {
            JOptionPane.showMessageDialog(null, "No hay productos en el sistema.");
            return;
        }

        String codigo = JOptionPane.showInputDialog("Ingresar el codigo del producto a buscar:");
        int indice = buscarIndiceCodigo(codigo);

        if (indice == -1) {
            JOptionPane.showMessageDialog(null, "No hay ningun producto con el sistema con el codigo: " + codigo + ".");
            return;
        }

        while (true) {
            int btn = mostrarBotones("Producto Encontrado con exito:\n" + productos[indice].getDatos(true), "Encontrado",
                    JOptionPane.QUESTION_MESSAGE, new String[]{"Eliminar", "Aumentar Stock", "Cancelar"});

            switch (btn) {
                case 0: // Eliminar
                    boolean sePuedeEliminar = true;
                    for (int i = 0; i < cantfacturas; i++) {
                        if (facturas[i].hayProductoFacturado(productos[indice])) {
                            sePuedeEliminar = false;
                            break;
                        }
                    }

                    if (sePuedeEliminar) {
                        btn = mostrarBotones("¿Seguro que quieres eliminar el siguiente producto?\n" + productos[indice].getDatos(true),
                                "Eliminar", JOptionPane.QUESTION_MESSAGE, new String[]{"SI", "NO"});
                        if (btn == 0) { // SI

                            for (int i = indice; i < cantProductos - 1; i++) {
                                productos[i] = productos[i + 1];
                            }

                            productos[cantProductos - 1] = null;
                            cantProductos--;

                            JOptionPane.showMessageDialog(null, "El producto se elimino con exito.");
                            return;
                        } else { // No, Cancel, X
                            JOptionPane.showMessageDialog(null, "Cancelacion de eliminacion.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El producto no se puede eliminar ya se facturo.");
                    }

                    break;
                case 1: // Aumentar Stock
                    int masStock = Integer.parseInt(JOptionPane.showInputDialog("Ingrese la cantidad a aumentar[" + productos[indice].getNombre() + "]:"));
                    productos[indice].setStock(productos[indice].getStock() + masStock);
                    JOptionPane.showMessageDialog(null, "Stock aumentado con éxito:\n" + productos[indice].getDatos(true));
                    break;
                case 2:
                    return;
            }
        }

    }

    public static void facturarCompra() {
        if (cantProductos == 0) {
            JOptionPane.showMessageDialog(null, "No hay productos en el sistema.");
            return;
        }

        if (cantfacturas == facturas.length) {
            JOptionPane.showMessageDialog(null, "La maxima cantidad de facturas que se puede"
                    + " gestionar es de: " + facturas.length + ".");
            return;
        }

        String nombre = JOptionPane.showInputDialog("Ingrese el nombre del cliente:");

        Factura nuevaFactura = new Factura(nombre);

        mostrarProductos();
        String codigo = null;

        while (true) {
            String comando = agregarProductoFactura(nuevaFactura, codigo);

            switch (comando.charAt(0)) {
                case '1': // Facturar otro producto.
                    codigo = null;
                    break;
                case '2': // Ingresar otra cantidad.
                    codigo = comando.substring(1);
                    break;
                case '3': // Cerrar Compra.
                    if (nuevaFactura.getCantDetalles() == 0) { // No se a agregado ningun producto
                        int btn = mostrarBotones("No se a agregado ningun producto\n¿Facturar producto o cancélar?", "Facturar",
                                JOptionPane.QUESTION_MESSAGE, new String[]{"Facturar producto", "Cancélar Compra"});
                        if (btn == 0) {
                            codigo = null;
                        } else {
                            JOptionPane.showMessageDialog(null, "Factura cancelada con exito.");
                            return;
                        }
                    } else {
                        nuevaFactura.cerrarFactura();
                        facturas[cantfacturas] = nuevaFactura;
                        cantfacturas++;
                        JOptionPane.showMessageDialog(null, nuevaFactura.getDatos());
                        return;
                    }
                    break;
                case '4': // Cancelar facturar.
                    Factura.consecutivoFactura();
                    JOptionPane.showMessageDialog(null, "Factura cancelada con exito.");
                    return;
            }

        }

    }

    public static void mostrarFacturas() {
        if (cantfacturas == 0) {
            JOptionPane.showMessageDialog(null, "No hay facturas en el sistema.");
            return;
        }

        // Check if the frame already exists and is visible
        if (facturasFrame != null && facturasFrame.isDisplayable()) {
            // Refresh the existing frame content
            refreshFacturasFrame();
            facturasFrame.toFront();
            facturasFrame.requestFocus();
            return;
        }

        // Create a JFrame to hold the grid and buttons
        facturasFrame = new JFrame("Lista de Facturas");
        facturasFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        facturasFrame.setSize(800, 500);
        facturasFrame.setLayout(new BorderLayout());

        // Add window listener to clear the reference when window is closed
        facturasFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                facturasFrame = null; // Clear the reference when window is closed
            }
        });

        // Create a table model with column names
        String[] columnNames = {"Número", "Cliente", "Fecha", "Total", "Acciones"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the actions column is editable
            }
        };

        // Add facturas to the table
        for (int i = 0; i < cantfacturas; i++) {
            Factura f = facturas[i];
            // Calculate total with tax
            double subtotal = 0;
            for (int j = 0; j < f.getCantDetalles(); j++) {
                subtotal += f.getDetallesFactura()[j].calcularSubtotal();
            }
            double totalConImpuesto = subtotal * 1.13; // Add 13% tax
            totalConImpuesto = Math.round(totalConImpuesto * 100.0) / 100.0;

            model.addRow(new Object[]{
                f.getNumero(),
                f.getCliente(),
                f.getFecha(),
                "¢" + totalConImpuesto,
                "Acciones"
            });
        }

        // Create the table and add it to a scroll pane
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        facturasFrame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for the action buttons at the bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // View Details button
        JButton viewButton = new JButton("Ver Detalles");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int numero = (Integer) model.getValueAt(selectedRow, 0);
                    int indice = buscarIndiceNumero(numero);
                    if (indice != -1) {
                        JOptionPane.showMessageDialog(facturasFrame, facturas[indice].getDatos());
                    }
                } else {
                    JOptionPane.showMessageDialog(facturasFrame, "Por favor, seleccione una factura para ver los detalles.");
                }
            }
        });
        buttonPanel.add(viewButton);

        // Back to Main Menu button
        JButton backButton = new JButton("Volver al Menú Principal");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuperF.showMainMenu(facturasFrame);
            }
        });
        buttonPanel.add(backButton);

        // Add the button panel to the frame
        facturasFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Número
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Cliente
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Fecha
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Acciones

        // Display the frame
        facturasFrame.setLocationRelativeTo(null);
        facturasFrame.setVisible(true);
    }

    // Method to refresh the existing facturas frame instead of creating a new one
    private static void refreshFacturasFrame() {
        if (facturasFrame == null) {
            return;
        }

        // Clear the existing content
        facturasFrame.getContentPane().removeAll();
        facturasFrame.setLayout(new BorderLayout());

        // Create a table model with column names
        String[] columnNames = {"Número", "Cliente", "Fecha", "Total", "Acciones"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only the actions column is editable
            }
        };

        // Add facturas to the table
        for (int i = 0; i < cantfacturas; i++) {
            Factura f = facturas[i];
            // Calculate total with tax
            double subtotal = 0;
            for (int j = 0; j < f.getCantDetalles(); j++) {
                subtotal += f.getDetallesFactura()[j].calcularSubtotal();
            }
            double totalConImpuesto = subtotal * 1.13; // Add 13% tax
            totalConImpuesto = Math.round(totalConImpuesto * 100.0) / 100.0;

            model.addRow(new Object[]{
                f.getNumero(),
                f.getCliente(),
                f.getFecha(),
                "¢" + totalConImpuesto,
                "Acciones"
            });
        }

        // Create the table and add it to a scroll pane
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        facturasFrame.add(scrollPane, BorderLayout.CENTER);

        // Create a panel for the action buttons at the bottom
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // View Details button
        JButton viewButton = new JButton("Ver Detalles");
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow >= 0) {
                    int numero = (Integer) model.getValueAt(selectedRow, 0);
                    int indice = buscarIndiceNumero(numero);
                    if (indice != -1) {
                        JOptionPane.showMessageDialog(facturasFrame, facturas[indice].getDatos());
                    }
                } else {
                    JOptionPane.showMessageDialog(facturasFrame, "Por favor, seleccione una factura para ver los detalles.");
                }
            }
        });
        buttonPanel.add(viewButton);

        // Back to Main Menu button
        JButton backButton = new JButton("Volver al Menú Principal");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SuperF.showMainMenu(facturasFrame);
            }
        });
        buttonPanel.add(backButton);

        // Add the button panel to the frame
        facturasFrame.add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);  // Número
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Cliente
        table.getColumnModel().getColumn(2).setPreferredWidth(150); // Fecha
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Total
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Acciones

        // Refresh the frame
        facturasFrame.revalidate();
        facturasFrame.repaint();
    }

    public static void buscarFacturas() {
        if (cantfacturas == 0) {
            JOptionPane.showMessageDialog(null, "No hay facturas en el sistema.");
            return;
        }

        int numero = Integer.parseInt(JOptionPane.showInputDialog("Ingresar el numero de la factura a buscar:"));
        int indice = buscarIndiceNumero(numero);

        if (indice == -1) {
            JOptionPane.showMessageDialog(null, "No hay ninguna factura con el sistema con el numero: " + numero + ".");
            return;
        }

        JOptionPane.showMessageDialog(null, facturas[indice].getDatos());
    }

    public static void generarDatos() {
        if (!generados) {
            Producto productosGenerar[] = new Producto[10];

            productosGenerar[0] = new Producto("PG01", "Café", "Café gourmet", 2500, 60);
            productosGenerar[1] = new Producto("PG02", "Chocolates", "Chocolates artesanales", 350, 50);
            productosGenerar[2] = new Producto("PG03", "Cerveza Imperial", "Cerveza tica", 1600, 200);
            productosGenerar[3] = new Producto("PG04", "Salsa Lizano", "Salsa típica tica", 1850, 150);
            productosGenerar[4] = new Producto("PG05", "Tapa Dulce", "Panela costarricense", 1800, 90);
            productosGenerar[5] = new Producto("PG06", "Café 1820", "Café molido", 2100, 110);
            productosGenerar[6] = new Producto("PG07", "Pan de Banano", "Pan dulce de banano", 900, 95);
            productosGenerar[7] = new Producto("PG08", "Leche Condensada", "Leche condensada", 1200, 130);
            productosGenerar[8] = new Producto("PG09", "Helados Pops", "Helados de diversos sabores", 550, 150);
            productosGenerar[9] = new Producto("PG10", "Jugo de Naranja", "Jugo natural Tropical", 875, 120);

            for (int i = 0; i < productosGenerar.length; i++) {
                if (buscarIndiceCodigo(productosGenerar[i].getCodigo()) == -1) {
                    if (cantProductos < productos.length) {
                        productos[cantProductos] = productosGenerar[i];
                        cantProductos++;
                    }
                }
            }

            String[] nombres = {
                "Mario Hernández",
                "Laura Gómez",
                "Carlos Rodríguez",
                "Ana Martínez",
                "Luis Fernández",
                "Elena Pérez",
                "Jorge Sánchez",
                "María López",
                "David Ramírez",
                "Lucía Torres",
                "Sergio González",
                "Carmen Morales"
            };

            Random r = new Random();
            int cantidadfacturas = r.nextInt(4, 9);

            for (int i = 0; i < cantidadfacturas; i++) {
                Factura nuevaFactura = new Factura(nombres[r.nextInt(nombres.length)]);
                int cantidadProductos = r.nextInt(2, 9);
                for (int j = 0; j < cantidadProductos; j++) {
                    int indiProducto = r.nextInt(cantidadProductos);
                    int cantElementos = r.nextInt(1, 5);
                    nuevaFactura.agregarDetalleFactura(new DetalleFactura(productos[indiProducto], cantElementos,
                            productos[indiProducto].getPrecio() * cantElementos));
                }
                nuevaFactura.cerrarFacturaGenerada();
                facturas[cantfacturas] = nuevaFactura;
                cantfacturas++;
            }

            generados = true;
            JOptionPane.showMessageDialog(null, "Datos generados con exito.");
        } else {
            JOptionPane.showMessageDialog(null, "Los datos ya fueron generados.");
        }
        SuperF.showMainMenu();

    }

    public static void viernesNegro() {
        if (descuento == 0) {

            descuento = Integer.parseInt(JOptionPane.showInputDialog("Ingrese el nuevo descuento:"));

            for (int i = 0; i < cantProductos; i++) {
                double desc = productos[i].getPrecio() - (productos[i].getPrecio() * (descuento / 100.0));
                productos[i].setPrecio(desc);
            }

            JOptionPane.showMessageDialog(null, "Viernes negro activo los precios tiene un " + descuento + " de descuento.");

        } else {
            int btn = mostrarBotones("¿Quieres seguir en viernes negro o cancelar el viernes negro?\n", "Eliminar",
                    JOptionPane.QUESTION_MESSAGE, new String[]{"Viernes Negro", "Cancelar el Viernes Negro"});
            if (btn == 0) { // SI
                JOptionPane.showMessageDialog(null, "Viernes negro activo los precios tiene un " + descuento + " de descuento.");
            } else { // No, Cancel, X

                for (int i = 0; i < cantProductos; i++) {
                    double precioAnterior = (productos[i].getPrecio() * 100) / (100 - descuento);
                    productos[i].setPrecio(precioAnterior);
                }

                descuento = 0;
                JOptionPane.showMessageDialog(null, "Viernes negro cancelado los precios vuelve a la normalidad.");
            }
        }
        SuperF.showMainMenu();
    }

    // METODOS GENERALES:
    private static String agregarProductoFactura(Factura factura, String codigo) {

        int btn;

        if (factura.getCantDetalles() == factura.getDetallesFactura().length) { // LIMITE DE PRODUCTOS ALCANZADO POR FACTURA
            btn = mostrarBotones("Límites de productos alcanzados por esta factura\n¿Cierre la factura o cancélar?", "Facturar",
                    JOptionPane.QUESTION_MESSAGE, new String[]{"Cerrar Compra", "Cancélar Compra"});
            if (btn == 0) {
                return "3"; // Cerrar Compra.
            } else {
                return "4";  // Cancelar Compra.
            }
        }

        if (codigo == null) {
            codigo = JOptionPane.showInputDialog("Ingresar el codigo del producto a comprar:");
        }

        int indice = buscarIndiceCodigo(codigo);

        if (indice == -1) { // NO EXITE EL PRODUCTO
            btn = mostrarBotones("No se encontró ningún producto con ese código: " + codigo + "\n¿Quieres ingresar otro producto?", "Facturar",
                    JOptionPane.QUESTION_MESSAGE, new String[]{"Facturar otro producto", "Cerrar Compra", "Cancélar Compra"});
            if (btn == 0) {
                return "1"; // Facturar otro producto.
            } else if (btn == 1) {
                return "3"; // Cerrar Compra.
            } else {
                return "4"; // Cancelar Compra.
            }
        }

        Producto producto = productos[indice];
        int cantidad = Integer.parseInt(JOptionPane.showInputDialog("Ingresar la cantidad ["
                + producto.getNombre() + " (" + producto.getStock() + ")] a comprar:"));

        if (cantidad > producto.getStock() || cantidad < 0) { // STOCK INSUFICIENTE o CANTIDAD CERO o NEGATIVA
            btn = mostrarBotones("Este producto no tiene suficientes unidades o la cantidad no es valida\n¿Quieres ingresar otro producto o otra cantidad?", "Facturar",
                    JOptionPane.QUESTION_MESSAGE, new String[]{"Facturar otro producto", "Ingresar otra cantidad", "Cerrar Compra", "Cancélar Compra"});
            if (btn == 0) {
                return "1"; // Facturar otro producto.
            } else if (btn == 1) {
                return "2" + codigo; // Ingresar otra cantidad.
            } else if (btn == 2) {
                return "3"; // Cerrar Compra.
            } else {
                return "4";// Cancelar facturar.
            }
        }

        double precio = producto.getPrecio() * cantidad;

        factura.agregarDetalleFactura(new DetalleFactura(producto, cantidad, precio));

        btn = mostrarBotones("¿Quieres facturar otro producto?", "Facturar",
                JOptionPane.QUESTION_MESSAGE, new String[]{"Facturar otro producto", "Cerrar Compra", "Cancélar Compra"});
        if (btn == 0) {
            return "1"; // Facturar otro producto.
        } else if (btn == 1) {
            return "3"; // Cerrar Compra.
        } else {
            return "4";// Cancelar facturar.
        }

    }

    private static int mostrarBotones(String mensaje, String titulo, int icon, String botones[]) {
        return JOptionPane.showOptionDialog(
                null,
                mensaje,
                titulo,
                JOptionPane.DEFAULT_OPTION,
                icon,
                null,
                botones,
                botones[0]);
    }

    private static int buscarIndiceCodigo(String codigo) {
        for (int i = 0; i < cantProductos; i++) {
            if (productos[i].getCodigo().equalsIgnoreCase(codigo)) {
                return i;
            }
        }
        return -1;
    }

    private static int buscarIndiceNumero(int numero) {
        for (int i = 0; i < cantfacturas; i++) {
            if (facturas[i].getNumero() == numero) {
                return i;
            }
        }
        return -1;
    }

    public static void modificarProducto(String codigo) {
        int indice = buscarIndiceCodigo(codigo);

        if (indice == -1) {
            JOptionPane.showMessageDialog(null, "No hay ningún producto en el sistema con el código: " + codigo + ".");
            return;
        }

        Producto p = productos[indice];

        try {
            String nombre = JOptionPane.showInputDialog("Ingrese el nuevo nombre del producto (actual: " + p.getNombre() + "):", p.getNombre());
            if (nombre == null) return; // User cancelled

            String descripcion = JOptionPane.showInputDialog("Ingrese la nueva descripción [" + nombre + "] (actual: " + p.getDescripcion() + "):", p.getDescripcion());
            if (descripcion == null) return; // User cancelled

            String precioStr = JOptionPane.showInputDialog("Ingrese el nuevo precio [" + nombre + "] (actual: ¢" + p.getPrecio() + "):", p.getPrecio());
            if (precioStr == null) return; // User cancelled
            double precio = Double.parseDouble(precioStr);

            String stockStr = JOptionPane.showInputDialog("Ingrese la nueva cantidad [" + nombre + "] (actual: " + p.getStock() + "):", p.getStock());
            if (stockStr == null) return; // User cancelled
            int stock = Integer.parseInt(stockStr);

            // Update the product
            p.setNombre(nombre);
            p.setDescripcion(descripcion);
            p.setPrecio(precio);
            p.setStock(stock);

            JOptionPane.showMessageDialog(null, "Producto modificado con éxito:\n" + p.getDatos(true));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error: Ingrese valores numéricos válidos para precio y stock.");
        }
    }
}
