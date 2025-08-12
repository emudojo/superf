package com.mycompany.superf;

public class DetalleFactura {

    private Producto producto;
    private int cantidad;
    private double precio;
    private static final double IMPUESTO = 0.13; // Tasa de impuesto del 13%

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
     * Devuelve los datos formateados con saltos de línea opcionales
     * @param conSaltoLinea si es true, agrega saltos de línea entre los campos
     * @return cadena formateada con los detalles del producto
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
     * Calcula el subtotal (precio sin impuesto)
     * @return subtotal redondeado a 2 decimales
     */
    public double calcularSubtotal() {
        return getPrecio();
    }

    /**
     * Calcula el precio total incluyendo el impuesto
     * @return precio total con impuesto, redondeado a 2 decimales
     */
    public double calcularTotal() {
        double total = getPrecio() * (1 + IMPUESTO);
        return Math.round(total * 100.0) / 100.0;
    }

    /**
     * Verifica si hay suficiente stock para la cantidad solicitada
     * @return true si hay suficiente stock, false en caso contrario
     */
    public boolean hayStockSuficiente() {
        return producto.getStock() >= cantidad;
    }

    /**
     * Actualiza el stock del producto restando la cantidad
     * @return true si el stock se actualizó correctamente, false si no hay suficiente stock
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
