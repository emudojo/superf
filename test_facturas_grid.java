import com.mycompany.superf.*;

public class test_facturas_grid {
    public static void main(String[] args) {
        System.out.println("[DEBUG_LOG] Testing mostrarFacturas grid functionality...");
        
        // Test 1: Show facturas when there are none
        System.out.println("[DEBUG_LOG] Test 1: Showing facturas when there are none");
        Gestion.mostrarFacturas();
        
        // Test 2: Generate some data and then show facturas
        System.out.println("[DEBUG_LOG] Test 2: Generating data and showing facturas");
        Gestion.generarDatos();
        
        // Wait a moment for the dialog to close
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // Now show facturas with data
        System.out.println("[DEBUG_LOG] Test 3: Showing facturas with data");
        Gestion.mostrarFacturas();
        
        System.out.println("[DEBUG_LOG] Test completed. Check if the grid interface appears correctly.");
    }
}