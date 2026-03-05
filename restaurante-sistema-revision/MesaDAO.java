import java.sql.*;
import java.util.ArrayList;

public class MesaDAO {
    
    public ArrayList<Mesa> obtenerTodas() {
        ArrayList<Mesa> mesas = new ArrayList<>();
        String sql = "SELECT * FROM mesas ORDER BY numero";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Mesa m = new Mesa(rs.getInt("numero"), rs.getInt("capacidad"));
                m.setEstado(rs.getString("estado"));
                // Valores por defecto para los campos que no están en la BD
                m.setMeseroAsignado("");
                m.setReservada(false);
                m.setReservadaPara("");
                mesas.add(m);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener mesas: " + e.getMessage());
        }
        return mesas;
    }

    public boolean insertar(Mesa mesa) {
        String sql = "INSERT INTO mesas (numero, capacidad, estado) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mesa.getNumero());
            stmt.setInt(2, mesa.getCapacidad());
            stmt.setString(3, mesa.getEstado());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al insertar mesa: " + e.getMessage());
            return false;
        }
    }
    
    public boolean actualizar(Mesa mesa) {
        String sql = "UPDATE mesas SET capacidad = ?, estado = ? WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, mesa.getCapacidad());
            stmt.setString(2, mesa.getEstado());
            stmt.setInt(3, mesa.getNumero());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar mesa: " + e.getMessage());
            return false;
        }
    }
    
    public boolean eliminar(int numero) {
        String sql = "DELETE FROM mesas WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al eliminar mesa: " + e.getMessage());
            return false;
        }
    }

    public Mesa obtenerPorNumero(int numero) {
        String sql = "SELECT * FROM mesas WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, numero);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Mesa m = new Mesa(rs.getInt("numero"), rs.getInt("capacidad"));
                m.setEstado(rs.getString("estado"));
                // Valores por defecto
                m.setMeseroAsignado("");
                m.setReservada(false);
                m.setReservadaPara("");
                return m;
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener mesa: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarEstado(int numero, String estado, String mesero) {
        String sql = "UPDATE mesas SET estado = ? WHERE numero = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, numero);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error actualizarEstado: " + e.getMessage());
            return false;
        }
    }
}