import java.sql.*;
import java.io.File;

public class DatabaseConnection {
    private static final String DB_NAME = "restaurante.db";
    private static final String URL = "jdbc:sqlite:" + DB_NAME;
    private static Connection connection = null;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Cargamos el driver
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(URL);
                
                // IMPORTANTE: Al conectar, inicializamos las tablas
                inicializarBaseDeDatos();
                
                System.out.println("✅ Conectado a SQLite (Archivo: " + DB_NAME + ")");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver SQLite no encontrado.");
        } catch (SQLException e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
        }
        return connection;
    }

    // ESTE ES EL MÉTODO QUE LE FALTABA A TU MAIN PARA COMPILAR
    public static boolean testConnection() {
        try {
            Connection c = getConnection();
            return (c != null && !c.isClosed());
        } catch (SQLException e) {
            return false;
        }
    }

    private static void inicializarBaseDeDatos() {
        try (Statement stmt = connection.createStatement()) {
            // TABLA USUARIOS: Estructura completa para compatibilidad
            String sqlUsuarios = 
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuario TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "nombre TEXT DEFAULT '', " +
                "rol TEXT NOT NULL, " +
                "activo INTEGER DEFAULT 1);";
            stmt.executeUpdate(sqlUsuarios);

            // TABLA PRODUCTOS
            String sqlProductos = 
                "CREATE TABLE IF NOT EXISTS productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT NOT NULL, " +
                "descripcion TEXT, " +
                "precio_venta REAL NOT NULL, " +
                "disponible INTEGER DEFAULT 1);";
            stmt.executeUpdate(sqlProductos);

            // TABLA MESAS
            String sqlMesas = 
                "CREATE TABLE IF NOT EXISTS mesas (" +
                "numero INTEGER PRIMARY KEY, " +
                "capacidad INTEGER NOT NULL, " +
                "estado TEXT DEFAULT 'Libre');";
            stmt.executeUpdate(sqlMesas);

            // TABLA ÓRDENES
            String sqlOrdenes = 
                "CREATE TABLE IF NOT EXISTS ordenes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "mesa_id INTEGER NOT NULL, " +
                "mesero TEXT NOT NULL, " +
                "estado TEXT DEFAULT 'Pendiente', " +
                "fecha DATETIME DEFAULT CURRENT_TIMESTAMP);";
            stmt.executeUpdate(sqlOrdenes);

            // TABLA DETALLE ÓRDENES
            String sqlDetalleOrdenes = 
                "CREATE TABLE IF NOT EXISTS detalle_orden (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_orden INTEGER NOT NULL, " +
                "id_producto INTEGER NOT NULL, " +
                "cantidad INTEGER NOT NULL, " +
                "notas TEXT, " +
                "estado TEXT DEFAULT 'Pendiente', " +
                "FOREIGN KEY (id_orden) REFERENCES ordenes(id), " +
                "FOREIGN KEY (id_producto) REFERENCES productos(id));";
            stmt.executeUpdate(sqlDetalleOrdenes);

            // Insertar datos iniciales solo si la tabla usuarios está vacía
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM usuarios");
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate("INSERT INTO usuarios (usuario, password, nombre, rol, activo) VALUES ('admin', 'admin', 'Administrador Principal', 'Administrador', 1)");
                stmt.executeUpdate("INSERT INTO usuarios (usuario, password, nombre, rol, activo) VALUES ('mesero', 'mesero', 'Juan Perez', 'Mesero', 1)");
                stmt.executeUpdate("INSERT INTO usuarios (usuario, password, nombre, rol, activo) VALUES ('cocina', 'cocina', 'Maria Garcia', 'Cocinero', 1)");
                stmt.executeUpdate("INSERT INTO mesas (numero, capacidad, estado) VALUES (1,4,'Libre'), (2,4,'Libre'), (3,6,'Libre'), (4,4,'Libre'), (5,2,'Libre'), (6,8,'Libre'), (7,4,'Libre'), (8,4,'Libre')");
                stmt.executeUpdate("INSERT INTO productos (nombre, descripcion, precio_venta, disponible) VALUES " +
                    "('Tacos al Pastor', '5 tacos con piña', 75.0, 1), " +
                    "('Hamburguesa', 'Carne de res con queso', 150.0, 1), " +
                    "('Refresco', 'Coca cola 600ml', 35.0, 1), " +
                    "('Papas Fritas', 'Porción mediana', 45.0, 1)");
                System.out.println("✅ Tablas creadas y datos iniciales insertados.");
            } else {
                // Verificar si las tablas de órdenes existen
                try {
                    ResultSet rsOrdenes = stmt.executeQuery("SELECT COUNT(*) FROM ordenes");
                    rsOrdenes.close();
                    System.out.println("✅ Tabla ordenes verificada");
                } catch (SQLException e) {
                    System.out.println("⚠️ La tabla ordenes no existe, creándola...");
                    stmt.executeUpdate(sqlOrdenes);
                    System.out.println("✅ Tabla ordenes creada");
                }
                
                try {
                    ResultSet rsDetalle = stmt.executeQuery("SELECT COUNT(*) FROM detalle_orden");
                    rsDetalle.close();
                    System.out.println("✅ Tabla detalle_orden verificada");
                } catch (SQLException e) {
                    System.out.println("⚠️ La tabla detalle_orden no existe, creándola...");
                    stmt.executeUpdate(sqlDetalleOrdenes);
                    System.out.println("✅ Tabla detalle_orden creada");
                }
            }

        } catch (SQLException e) {
            System.out.println("⚠️ Nota en inicialización: " + e.getMessage());
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar: " + e.getMessage());
        }
    }
}