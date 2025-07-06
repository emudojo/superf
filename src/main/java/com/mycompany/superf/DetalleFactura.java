package com.mycompany.superf;

public class DetalleFactura {

    private Producto producto;
    private int cantidad;
    private double precio;
    private static final double IMPUESTO = 0.13; // 13% tax rate

    public DetalleFactura(Producto producto, int cantidad, double precio) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public String getDatos() {
        return "Producto: [" + producto.getNombre() + "] Precio Unitario: [¢" + producto.getPrecio()
                + "] Cantidad:[" + cantidad + "] Precio: [¢" + getPrecio() + "]";
    }

    /**
     * Returns formatted data with customizable line breaks
     * @param conSaltoLinea if true, adds line breaks between fields
     * @return formatted string with product details
     */
    public String getDatos(boolean conSaltoLinea) {
        String saltoLinea = " ";

        if (conSaltoLinea) {
            saltoLinea = "\n";
        }

        return "Producto: [" + producto.getNombre() + "]" 
                + saltoLinea + "Precio Unitario: [¢" + producto.getPrecio() + "]" 
                + saltoLinea + "Cantidad: [" + cantidad + "]" 
                + saltoLinea + "Precio: [¢" + getPrecio() + "]";
    }

    /**
     * Calculates the subtotal (price without tax)
     * @return subtotal rounded to 2 decimal places
     */
    public double calcularSubtotal() {
        return getPrecio();
    }

    /**
     * Calculates the total price including tax
     * @return total price with tax, rounded to 2 decimal places
     */
    public double calcularTotal() {
        double total = getPrecio() * (1 + IMPUESTO);
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Checks if there's enough stock for the requested quantity
     * @return true if there's enough stock, false otherwise
     */
    public boolean hayStockSuficiente() {
        return producto.getStock() >= cantidad;
    }

    /**
     * Updates the product stock by subtracting the quantity
     * @return true if the stock was updated successfully, false if there's not enough stock
     */
    public boolean actualizarStock() {
        if (hayStockSuficiente()) {
            producto.setStock(producto.getStock() - cantidad);
            return true;
        }
        return false;
    }

    public double getPrecio() {
        return Math.round(precio * 100.0) / 100.0;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

}
