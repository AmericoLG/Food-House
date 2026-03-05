import java.sql.*;
import java.util.ArrayList;

public class UsuarioDAO {
    
    // LOGIN: Ajustado estrictamente a tu tabla SQLite y a tu modelo de 4 parámetros
    public Usuario login(String userStr, String passStr) {
        // En SQLite tu columna se llama 'usuario'
        String sql = "SELECT id, usuario, password, rol FROM usuarios WHERE usuario = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) return null;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, userStr);
                stmt.setString(2, passStr);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // Usamos tu constructor de 4 parámetros:
                        // public Usuario(String username, String password, String nombre, String rol)
                        Usuario u = new Usuario(
                            rs.getString("usuario"), 
                            rs.getString("password"),
                            rs.getString("usuario"), // Pasamos usuario como nombre para evitar el error de columna
                            rs.getString("rol")
                        );
                        u.setId(rs.getInt("id"));
                        return u;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en UsuarioDAO.login: " + e.getMessage());
        }
        return null;
    }

    // OBTENER TODOS: Para llenar tus listas de Database.java
    public ArrayList<Usuario> obtenerTodos() {
        ArrayList<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, usuario, password, rol FROM usuarios";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Usamos el constructor de 4 parámetros
                Usuario u = new Usuario(
                    rs.getString("usuario"),
                    rs.getString("password"),
                    rs.getString("usuario"), // nombre = usuario
                    rs.getString("rol")
                );
                u.setId(rs.getInt("id"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error en obtenerTodos: " + e.getMessage());
        }
        return lista;
    }

    // INSERTAR: Basado en tu CREATE TABLE
    public boolean insertar(Usuario u) {
        String sql = "INSERT INTO usuarios (usuario, password, rol) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, u.getUsuario());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getRol());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("❌ Error al insertar: " + e.getMessage());
            return false;
        }
    }

    // ACTUALIZAR
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET password = ?, rol = ? WHERE usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, u.getPassword());
            ps.setString(2, u.getRol());
            ps.setString(3, u.getUsuario());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // ELIMINAR
    public boolean eliminar(String ident) {
        String sql = "DELETE FROM usuarios WHERE usuario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ident);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}