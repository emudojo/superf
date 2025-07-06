package com.mycompany.superf;

import java.time.LocalDateTime;

public class Factura {

    private int numero;
    private String cliente;
    private LocalDateTime fecha;
    private DetalleFactura detallesFactura[];
    private int cantDetalles;
    private double total;
    private static int consecutivoFactura = 5000;

    public Factura(String cliente) {
        this.numero = consecutivoFactura;
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.detallesFactura = new DetalleFactura[10];
        this.cantDetalles = 0;
        this.total = 0;
        consecutivoFactura++;
    }

    public boolean hayProductoFacturado(Producto producto) {
        for (int i = 0; i < cantDetalles; i++) {
            if (detallesFactura[i].getProducto().getCodigo().equals(producto.getCodigo())) {
                return true;
            }
        }
        return false;
    }

    public void agregarDetalleFactura(DetalleFactura detalleFactura) {
        detallesFactura[cantDetalles] = detalleFactura;
        cantDetalles++;
    }

    public void cerrarFactura() {
        for (int i = 0; i < cantDetalles; i++) {
            // Update stock using the new method
            detallesFactura[i].actualizarStock();

            // Create a copy of the product
            Producto p = detallesFactura[i].getProducto();
            detallesFactura[i].setProducto(new Producto(p.getCodigo(), "COPY-" + p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock()));

            // Add the subtotal to the total
            this.total += detallesFactura[i].calcularSubtotal();
        }
    }

    public void cerrarFacturaGenerada() {
        for (int i = 0; i < cantDetalles; i++) {
            // Create a copy of the product
            Producto p = detallesFactura[i].getProducto();
            detallesFactura[i].setProducto(new Producto(p.getCodigo(), "COPY-" + p.getNombre(), p.getDescripcion(), p.getPrecio(), p.getStock()));

            // Add the subtotal to the total
            this.total += detallesFactura[i].calcularSubtotal();
        }
    }

    public String getDatos() {
        String datos = " ************************* [FACTURA #" + numero + "] *************************\n"
                + "Cliente: [" + cliente + "] Fecha: [" + getFecha() + "]\n\n";

        double subtotal = 0;
        double totalConImpuesto = 0;

        for (int i = 0; i < cantDetalles; i++) {
            datos += (i + 1) + "- " + detallesFactura[i].getDatos() + "\n";
            subtotal += detallesFactura[i].calcularSubtotal();
            totalConImpuesto += detallesFactura[i].calcularTotal();
        }

        // Round to 2 decimal places
        subtotal = Math.round(subtotal * 100.0) / 100.0;
        totalConImpuesto = Math.round(totalConImpuesto * 100.0) / 100.0;
        double impuesto = Math.round((totalConImpuesto - subtotal) * 100.0) / 100.0;

        datos += "Subtotal: [¢" + subtotal + "]\n";
        datos += "Impuesto (13%): [¢" + impuesto + "]\n";
        datos += "Total a pagar: [¢" + totalConImpuesto + "]\n\n";

        return datos;
    }

    public double getTotal() {
        return Math.round(total * 100.0) / 100.0;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getFecha() {
        return fecha.toString().substring(0, 16).replace("T", " ");
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public DetalleFactura[] getDetallesFactura() {
        return detallesFactura;
    }

    public void setDetallesFactura(DetalleFactura[] detallesFactura) {
        this.detallesFactura = detallesFactura;
    }

    public int getCantDetalles() {
        return cantDetalles;
    }

    public void setCantDetalles(int cantDetalles) {
        this.cantDetalles = cantDetalles;
    }

    public static void consecutivoFactura() {
        consecutivoFactura--;
    }

}
