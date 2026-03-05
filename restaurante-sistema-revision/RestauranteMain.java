import javax.swing.*;

public class RestauranteMain {
    public static void main(String[] args) {
        // 1. Estilo del sistema operativo (Muy bien!)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error de look and feel: " + e.getMessage());
        }

        // 2. INICIALIZAR SQLITE (Paso clave para portabilidad)
        // Esto crea el archivo restaurante.db y las tablas la primera vez
        System.out.println("Iniciando base de datos local...");
        if (DatabaseConnection.testConnection()) {
            System.out.println("✅ Base de datos lista.");
        } else {
            JOptionPane.showMessageDialog(null, 
                "Error al conectar con la base de datos local.\nVerifique el Driver SQLite.", 
                "Error Crítico", JOptionPane.ERROR_MESSAGE);
        }
        
        // 3. Iniciar aplicación
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VentanaLogin().setVisible(true);
            }
        });
    }
}