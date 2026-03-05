import java.sql.*;
import java.util.ArrayList;

public class OrdenDAO {
    
    public int crearOrden(int mesaId, String meseroNombre) {
        String sql = "INSERT INTO ordenes (mesa_id, mesero, estado) VALUES (?, ?, 'Pendiente')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, mesaId);
            stmt.setString(2, meseroNombre);
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println("Error crearOrden SQLite: " + e.getMessage());
        }
        return -1;
    }

    public boolean agregarItem(int ordenId, Producto producto, int cantidad, String notas) {
        String sql = "INSERT INTO detalle_orden (id_orden, id_producto, cantidad, notas, estado) VALUES (?, ?, ?, ?, 'Pendiente')";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, ordenId);
            stmt.setInt(2, producto.getId());
            stmt.setInt(3, cantidad);
            stmt.setString(4, notas);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error agregarItem: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Orden> obtenerActivas() {
        ArrayList<Orden> ordenes = new ArrayList<>();
        // Query simplificada para coincidir con tu script de SQLite
        String sql = "SELECT o.*, m.numero, m.capacidad FROM ordenes o " +
                     "JOIN mesas m ON o.mesa_id = m.numero WHERE o.estado = 'Pendiente'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("numero"), rs.getInt("capacidad"));
                Orden orden = new Orden(rs.getInt("id"), mesa, rs.getString("mesero"));
                orden.setEstado(rs.getString("estado"));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            System.out.println("Error obtenerActivas: " + e.getMessage());
        }
        return ordenes;
    }
}