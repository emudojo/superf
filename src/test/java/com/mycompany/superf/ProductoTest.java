package com.mycompany.superf;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Pruebas de Producto usando TestNG.
 */
public class ProductoTest {

    @Test(description = "Debe crear un producto y exponer correctamente sus getters")
    public void testCrearProductoYGetters() {
        Producto p = new Producto("P001", "Arroz", "Arroz 1kg", 1490.0, 25);

        Assert.assertEquals(p.getCodigo(), "P001", "Código incorrecto");
        Assert.assertEquals(p.getNombre(), "Arroz", "Nombre incorrecto");
        Assert.assertEquals(p.getDescripcion(), "Arroz 1kg", "Descripción incorrecta");
        Assert.assertEquals(p.getPrecio(), 1490.0, 0.0, "Precio incorrecto");
        Assert.assertEquals(p.getStock(), 25, "Stock incorrecto");

        String datos = p.getDatos(true);
        Assert.assertTrue(datos.contains("Codigo: [P001]"));
        Assert.assertTrue(datos.contains("Nombre: [Arroz]"));
        Assert.assertTrue(datos.contains("Descripcion: [Arroz 1kg]"));
    }

    @Test(description = "El precio debe redondearse a 2 decimales en getPrecio()")
    public void testPrecioRedondeo() {
        Producto p = new Producto("P002", "Leche", "Leche 1L", 1234.5678, 10);
        // getPrecio redondea a 2 decimales
        Assert.assertEquals(p.getPrecio(), 1234.57, 0.0);

        p.setPrecio(1234.564);
        Assert.assertEquals(p.getPrecio(), 1234.56, 0.0);

        p.setPrecio(1234.565);
        Assert.assertEquals(p.getPrecio(), 1234.57, 0.0);
    }

    @Test(description = "Debe validar el stock suficiente en DetalleFactura")
    public void testHayStockSuficiente() {
        Producto p = new Producto("P003", "Huevos", "Docena", 1500.0, 6);
        DetalleFactura df = new DetalleFactura(p, 5, 5 * p.getPrecio());
        Assert.assertTrue(df.hayStockSuficiente(), "Debería haber stock suficiente");

        DetalleFactura df2 = new DetalleFactura(p, 7, 7 * p.getPrecio());
        Assert.assertFalse(df2.hayStockSuficiente(), "No debería haber stock suficiente");
    }

    @Test(description = "Debe actualizar el stock del producto al cerrar la factura")
    public void testActualizarStockConCerrarFactura() {
        Producto p = new Producto("P004", "Frijoles", "Frijoles negros 1kg", 2000.0, 10);
        Factura factura = new Factura("Cliente Test");

        DetalleFactura d1 = new DetalleFactura(p, 3, 3 * p.getPrecio());
        DetalleFactura d2 = new DetalleFactura(p, 2, 2 * p.getPrecio());
        factura.agregarDetalleFactura(d1);
        factura.agregarDetalleFactura(d2);

        // Al cerrar factura, se debe actualizar stock en cada detalle
        factura.cerrarFactura();

        // Se restan 3 y 2 unidades del producto original
        Assert.assertEquals(p.getStock(), 5, "El stock del producto debería disminuir a 5");

        // Validar el total calculado (subtotal sin impuesto sumado en Factura.cerrarFactura)
        double esperadoSubtotal = d1.getPrecio() + d2.getPrecio();
        Assert.assertEquals(factura.getTotal(), Math.round(esperadoSubtotal * 100.0) / 100.0, 0.0);
    }

    @Test(description = "calcularTotal en DetalleFactura debe aplicar 13% de impuesto")
    public void testCalcularTotalConImpuesto() {
        Producto p = new Producto("P005", "Azúcar", "1kg", 1000.0, 100);
        DetalleFactura d = new DetalleFactura(p, 2, 2 * p.getPrecio()); // subtotal 2000.00
        // total con 13% = 2260.00
        Assert.assertEquals(d.calcularTotal(), 2260.00, 0.0);
    }
}
