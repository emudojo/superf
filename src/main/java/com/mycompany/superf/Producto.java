package com.mycompany.superf;

public class Producto {

    private String codigo;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;

    public Producto(String codigo, String nombre, String descripcion, double precio, int stock) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public String getDatos(boolean conSaltoLinea) {
        String saltoLinea = " ";

        if (conSaltoLinea) {
            saltoLinea = "\n";
        }

        String datos = "Codigo: [" + codigo + "]" 
                + saltoLinea + "Nombre: [" + nombre + "]" 
                + saltoLinea + "Descripcion: [" + descripcion + "]" 
                + saltoLinea + "precio: [¢" + getPrecio() + "]"
                + saltoLinea + "Stock: [" + stock + "]";
        
        return datos;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return (double) Math.round(precio * 100) / 100;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

}
