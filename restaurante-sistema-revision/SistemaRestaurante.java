import java.sql.*;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class SistemaRestaurante {
    private Usuario usuarioActual;
    private Connection conexion;
    private Orden ordenActual;
    private UsuarioDAO usuarioDAO;
    
    public SistemaRestaurante() {
        conectarBD();
        usuarioDAO = new UsuarioDAO();
    }
    
    private void conectarBD() {
        try {
            // Cambio para SQLite: Usamos el driver de SQLite
            Class.forName("org.sqlite.JDBC");
            
            // Cambio para SQLite: Usamos la ruta del archivo local
            String url = "jdbc:sqlite:restaurante.db";
            
            System.out.println("Conectando a: " + url);
            
            conexion = DriverManager.getConnection(url);
            System.out.println("Conexión exitosa a SQLite!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: Driver SQLite no encontrado");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
            JOptionPane.showMessageDialog(null, 
                "No se puede conectar a la base de datos SQLite.\n" +
                "Error: " + e.getMessage(), 
                "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // ==================== LOGIN Y SESIÓN ====================
    
    public boolean login(String usuario, String password) {
        Usuario u = usuarioDAO.login(usuario, password);
        if (u != null) {
            usuarioActual = u;
            return true;
        }
        return false;
    }
    
    public void logout() {
        usuarioActual = null;
        ordenActual = null;
    }
    
    public String getRolUsuario() {
        return usuarioActual != null ? usuarioActual.getRol() : null;
    }
    
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
    
    public boolean esAdmin() {
        return usuarioActual != null && "Administrador".equals(usuarioActual.getRol());
    }
    
    public boolean esMesero() {
        return usuarioActual != null && "Mesero".equals(usuarioActual.getRol());
    }
    
    public boolean esCocinero() {
        return usuarioActual != null && "Cocinero".equals(usuarioActual.getRol());
    }
    
    public Connection getConexion() {
        return conexion;
    }
    
    // ==================== MÉTODOS DE MESAS ====================
    
    public ArrayList<Mesa> getMesas() {
        ArrayList<Mesa> mesas = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT * FROM mesas ORDER BY numero";
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Mesa m = new Mesa(rs.getInt("numero"), rs.getInt("capacidad"));
                m.setEstado(rs.getString("estado"));
                // Valores por defecto para campos que no existen en la BD
                m.setMeseroAsignado("");
                m.setReservada(false);
                m.setReservadaPara("");
                mesas.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return mesas;
    }
    
    public ArrayList<Mesa> getMesasDisponibles() {
        ArrayList<Mesa> mesas = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // Consulta corregida - tabla mesas solo tiene: numero, capacidad, estado
            String sql = "SELECT * FROM mesas WHERE estado = 'Libre' ORDER BY numero";
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Mesa m = new Mesa(rs.getInt("numero"), rs.getInt("capacidad"));
                m.setEstado(rs.getString("estado"));
                mesas.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return mesas;
    }
    
    public void editarCapacidadMesa(int numeroMesa, int nuevaCapacidad) throws SQLException {
        PreparedStatement ps = null;
        try {
            String sql = "UPDATE mesas SET capacidad = ? WHERE numero = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, nuevaCapacidad);
            ps.setInt(2, numeroMesa);
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement: " + e.getMessage());
                }
            }
        }
    }
    
    public void agregarMesa(int numero, int capacidad) throws SQLException {
        PreparedStatement ps = null;
        try {
            // Consulta corregida - tabla mesas solo tiene: numero, capacidad, estado
            String sql = "INSERT INTO mesas (numero, capacidad, estado) VALUES (?, ?, 'Libre')";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, numero);
            ps.setInt(2, capacidad);
            ps.executeUpdate();
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement: " + e.getMessage());
                }
            }
        }
    }
    
    public void eliminarMesa(int numeroMesa) throws SQLException {
        PreparedStatement ps = null;
        try {
            // Consulta corregida - tabla mesas solo tiene: numero, capacidad, estado
            String sql = "DELETE FROM mesas WHERE numero = ? AND estado = 'Libre'";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, numeroMesa);
            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se puede eliminar la mesa. Puede estar ocupada o no existir.");
            }
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar PreparedStatement: " + e.getMessage());
                }
            }
        }
    }
    
    // ==================== MÉTODOS DE RESERVAS (CORREGIDOS) ====================
    
    public void reservarMesa(int numeroMesa, String nombreCliente) throws SQLException {
        PreparedStatement psCheck = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try {
            String sqlCheck = "SELECT estado, reservada FROM mesas WHERE numero = ?";
            psCheck = conexion.prepareStatement(sqlCheck);
            psCheck.setInt(1, numeroMesa);
            rs = psCheck.executeQuery();
            
            if (!rs.next()) {
                throw new SQLException("Mesa no encontrada: " + numeroMesa);
            }
            
            String estadoMesa = rs.getString("estado");
            boolean reservada = rs.getBoolean("reservada");
            
            if (!"Libre".equals(estadoMesa) || reservada) {
                throw new SQLException("Mesa no disponible para reserva. Estado: " + estadoMesa);
            }
            
            String sql = "UPDATE mesas SET reservada = 1, reservada_para = ?, estado = 'Reservada' WHERE numero = ?";
            ps = conexion.prepareStatement(sql);
            ps.setString(1, nombreCliente);
            ps.setInt(2, numeroMesa);
            ps.executeUpdate();
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (psCheck != null) psCheck.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    public void cancelarReservaMesa(int numeroMesa) throws SQLException {
        PreparedStatement psCheck = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        
        try {
            String sqlCheck = "SELECT reservada FROM mesas WHERE numero = ?";
            psCheck = conexion.prepareStatement(sqlCheck);
            psCheck.setInt(1, numeroMesa);
            rs = psCheck.executeQuery();
            
            if (!rs.next() || rs.getInt("reservada") == 0) {
                throw new SQLException("La mesa no está reservada");
            }
            
            String sql = "UPDATE mesas SET reservada = 0, reservada_para = NULL, estado = 'Libre' WHERE numero = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, numeroMesa);
            ps.executeUpdate();
            
        } finally {
            try {
                if (rs != null) rs.close();
                if (psCheck != null) psCheck.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    public void marcarMesaPagada(int numeroMesa) throws SQLException {
        PreparedStatement ps = null;
        PreparedStatement psOrden = null;
        
        try {
            String sql = "UPDATE mesas SET estado = 'Libre', mesero_asignado = NULL, reservada = 0, reservada_para = NULL WHERE numero = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, numeroMesa);
            int filas = ps.executeUpdate();
            
            if (filas == 0) {
                throw new SQLException("No se pudo liberar la mesa");
            }
            
            String sqlOrden = "UPDATE ordenes SET estado = 'Pagada' WHERE mesa_id = ? AND estado = 'Pendiente'";
            psOrden = conexion.prepareStatement(sqlOrden);
            psOrden.setInt(1, numeroMesa);
            psOrden.executeUpdate();
            
        } finally {
            try {
                if (ps != null) ps.close();
                if (psOrden != null) psOrden.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    // ==================== MÉTODOS DE PRODUCTOS ====================
    
    public ArrayList<Producto> getMenu() {
        ArrayList<Producto> productos = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            // SQLite usa 1 para true
            String sql = "SELECT * FROM productos WHERE disponible = 1 ORDER BY nombre";
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio_venta"),
                    rs.getInt("disponible") == 1
                );
                productos.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return productos;
    }
    
    // ==================== MÉTODOS DE ÓRDENES ====================
    
    public Orden getOrdenActual() {
        return ordenActual;
    }
    
    public Orden crearOrden(int numeroMesa) {
        PreparedStatement psCheck = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        ResultSet generatedKeys = null;
        PreparedStatement psMesa = null;
        
        try {
            String sqlCheck = "SELECT estado FROM mesas WHERE numero = ?";
            psCheck = conexion.prepareStatement(sqlCheck);
            psCheck.setInt(1, numeroMesa);
            rs = psCheck.executeQuery();
            
            if (!rs.next()) {
                System.out.println("Mesa no encontrada: " + numeroMesa);
                return null;
            }
            
            String estadoMesa = rs.getString("estado");
            
            if (!"Libre".equals(estadoMesa)) {
                System.out.println("Mesa no disponible. Estado: " + estadoMesa);
                return null;
            }
            
            // Cambio para SQLite: datetime('now','localtime') en lugar de NOW()
            String sql = "INSERT INTO ordenes (mesa_id, mesero, estado, fecha) VALUES (?, ?, 'Pendiente', datetime('now','localtime'))";
            ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, numeroMesa);
            ps.setString(2, usuarioActual.getUsuario());
            ps.executeUpdate();
            
            generatedKeys = ps.getGeneratedKeys();
            int ordenId = 0;
            if (generatedKeys.next()) {
                ordenId = generatedKeys.getInt(1);
            }
            
            // Actualizar estado de la mesa a Ocupada
            String sqlMesa = "UPDATE mesas SET estado = 'Ocupada' WHERE numero = ?";
            psMesa = conexion.prepareStatement(sqlMesa);
            psMesa.setInt(1, numeroMesa);
            psMesa.executeUpdate();
            
            // Obtener información de la mesa
            Mesa mesa = null;
            ArrayList<Mesa> mesas = getMesas();
            for (Mesa m : mesas) {
                if (m.getNumero() == numeroMesa) {
                    mesa = m;
                    mesa.setEstado("Ocupada");
                    break;
                }
            }
            
            if (mesa == null) {
                System.out.println("Error: No se encontró la mesa " + numeroMesa);
                return null;
            }
            
            // Crear objeto Orden
            Orden orden = new Orden(ordenId, mesa, usuarioActual.getUsuario());
            ordenActual = orden;
            
            System.out.println("✅ Orden creada exitosamente: ID=" + ordenId + ", Mesa=" + numeroMesa);
            return orden;
            
        } catch (SQLException e) {
            System.out.println("Error al crear orden: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (rs != null) rs.close();
                if (psCheck != null) psCheck.close();
                if (generatedKeys != null) generatedKeys.close();
                if (ps != null) ps.close();
                if (psMesa != null) psMesa.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    public void agregarItemAOrden(Producto producto, int cantidad, String notas) {
        System.out.println("=== DEPURACIÓN: agregarItemAOrden() INICIADO ===");
        System.out.println("ordenActual: " + (ordenActual != null ? "ID: " + ordenActual.getId() : "NULL"));
        System.out.println("producto: " + (producto != null ? producto.getNombre() : "NULL"));
        System.out.println("cantidad: " + cantidad);
        System.out.println("notas: " + (notas != null ? notas : "SIN NOTAS"));
        
        if (ordenActual == null) {
            System.out.println("ERROR: No hay orden activa para agregar item");
            return;
        }
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "INSERT INTO detalle_orden (id_orden, id_producto, cantidad, notas, estado) VALUES (?, ?, ?, ?, 'Pendiente')";
            System.out.println("Ejecutando SQL: " + sql);
            System.out.println("Parámetros - id_orden: " + ordenActual.getId() + ", id_producto: " + producto.getId() + ", cantidad: " + cantidad + ", notas: " + notas);
            
            ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, ordenActual.getId());
            ps.setInt(2, producto.getId());
            ps.setInt(3, cantidad);
            ps.setString(4, notas);
            
            int resultado = ps.executeUpdate();
            System.out.println("Resultado INSERT: " + resultado + " filas afectadas");
            
            rs = ps.getGeneratedKeys();
            int itemId = 0;
            if (rs.next()) {
                itemId = rs.getInt(1);
                System.out.println("ID de detalle_orden generado: " + itemId);
            } else {
                System.out.println("ADVERTENCIA: No se pudo obtener ID generado");
            }
            
            ItemOrden item = new ItemOrden(itemId, producto, cantidad, notas);
            ordenActual.agregarItem(item);
            System.out.println("Item agregado exitosamente a la orden");
            System.out.println("=== DEPURACIÓN: agregarItemAOrden() FINALIZADO ===");
            
        } catch (SQLException e) {
            System.out.println("ERROR SQL en agregarItemAOrden: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    public void finalizarOrden() {
        ordenActual = null;
    }
    
    // ==================== MÉTODOS PARA COCINA (CORREGIDOS) ====================
    
    public ArrayList<ItemOrden> getItemsPendientesCocina() {
        ArrayList<ItemOrden> items = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT d.id, d.cantidad, d.notas, d.estado, d.id_producto, " +
                        "p.nombre as prod_nombre, p.descripcion, p.precio_venta, " +
                        "o.mesa_id, u.nombre as mesero_nombre " +
                        "FROM detalle_orden d " +
                        "JOIN productos p ON d.id_producto = p.id " +
                        "JOIN ordenes o ON d.id_orden = o.id " +
                        "LEFT JOIN usuarios u ON o.mesero = u.usuario " +
                        "WHERE d.estado IN ('Pendiente', 'En espera', 'Preparando', 'Listo') " +
                        "AND o.estado = 'Pendiente' " +
                        "ORDER BY d.id ASC";
            
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("prod_nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio_venta"),
                    true
                );
                
                ItemOrden item = new ItemOrden(
                    rs.getInt("id"),
                    p,
                    rs.getInt("cantidad"),
                    rs.getString("notas")
                );
                item.setEstado(rs.getString("estado"));
                item.setMesaId(rs.getInt("mesa_id"));
                item.setMesero(rs.getString("mesero_nombre") != null ? rs.getString("mesero_nombre") : "Desconocido");
                items.add(item);
            }
        } catch (SQLException e) {
            System.out.println("Error en getItemsPendientesCocina: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return items;
    }
    
    public boolean actualizarEstadoItemCocina(int itemId, String nuevoEstado) {
        PreparedStatement psUpdate = null;
        PreparedStatement psGetOrden = null;
        ResultSet rsOrden = null;
        PreparedStatement psCheckItems = null;
        ResultSet rsCheck = null;
        PreparedStatement psUpdateOrden = null;
        
        try {
            // 1. Actualizar el estado del item
            String sqlUpdate = "UPDATE detalle_orden SET estado = ? WHERE id = ?";
            psUpdate = conexion.prepareStatement(sqlUpdate);
            psUpdate.setString(1, nuevoEstado);
            psUpdate.setInt(2, itemId);
            int filas = psUpdate.executeUpdate();
            
            if (filas > 0) {
                // 2. Obtener el ID de la orden del item actualizado
                String sqlGetOrdenId = "SELECT id_orden FROM detalle_orden WHERE id = ?";
                psGetOrden = conexion.prepareStatement(sqlGetOrdenId);
                psGetOrden.setInt(1, itemId);
                rsOrden = psGetOrden.executeQuery();
                
                if (rsOrden.next()) {
                    int ordenId = rsOrden.getInt("id_orden");
                    
                    // 3. Verificar si TODOS los items de esa orden están completados
                    String sqlCheckItems = "SELECT COUNT(*) as total_items, " +
                                               "SUM(CASE WHEN d.estado IN ('Listo', 'Entregado') THEN 1 ELSE 0 END) as items_completados " +
                                               "FROM detalle_orden d WHERE d.id_orden = ?";
                    psCheckItems = conexion.prepareStatement(sqlCheckItems);
                    psCheckItems.setInt(1, ordenId);
                    rsCheck = psCheckItems.executeQuery();
                    
                    if (rsCheck.next()) {
                        int totalItems = rsCheck.getInt("total_items");
                        int itemsCompletados = rsCheck.getInt("items_completados");
                        
                        // 4. Si TODOS los items están completados, marcar la orden como pagada
                        if (totalItems == itemsCompletados) {
                            String sqlUpdateOrden = "UPDATE ordenes SET estado = 'Pagada' WHERE id = ?";
                            psUpdateOrden = conexion.prepareStatement(sqlUpdateOrden);
                            psUpdateOrden.setInt(1, ordenId);
                            psUpdateOrden.executeUpdate();
                            
                            // Liberar la mesa asociada a la orden
                            try {
                                String sqlMesa = "UPDATE mesas SET estado = 'Libre' WHERE numero = (SELECT mesa_id FROM ordenes WHERE id = ?)";
                                PreparedStatement psMesa = conexion.prepareStatement(sqlMesa);
                                psMesa.setInt(1, ordenId);
                                psMesa.executeUpdate();
                                psMesa.close();
                                
                                System.out.println("✅ Mesa liberada automáticamente al marcar orden #" + ordenId + " como Pagada");
                            } catch (SQLException e) {
                                System.out.println("⚠️ Error liberando mesa: " + e.getMessage());
                            }
                            
                            System.out.println("✅ Orden #" + ordenId + " completada y marcada como Pagada");
                            System.out.println("🎉 Todos los items de la orden han sido preparados y entregados");
                        } else {
                            System.out.println("📝 Orden #" + ordenId + ": " + itemsCompletados + "/" + totalItems + " items completados");
                        }
                    }
                }
            }
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error en actualizarEstadoItemCocina: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Cerrar todos los recursos en orden inverso a su creación
            try {
                if (rsCheck != null) rsCheck.close();
                if (rsOrden != null) rsOrden.close();
                if (psUpdateOrden != null) psUpdateOrden.close();
                if (psCheckItems != null) psCheckItems.close();
                if (psGetOrden != null) psGetOrden.close();
                if (psUpdate != null) psUpdate.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    // ==================== MÉTODOS DE ÓRDENES PARA ADMIN ====================
    
    public ArrayList<Orden> getTodasLasOrdenes() {
        ArrayList<Orden> ordenes = new ArrayList<>();
        try {
            String sql = "SELECT o.id, o.mesa_id, o.mesero, o.estado, o.fecha, m.capacidad " +
                        "FROM ordenes o JOIN mesas m ON o.mesa_id = m.numero " +
                        "ORDER BY o.id DESC";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("mesa_id"), rs.getInt("capacidad"));
                Orden orden = new Orden(rs.getInt("id"), mesa, rs.getString("mesero"));
                orden.setEstado(rs.getString("estado"));
                orden.setFecha(rs.getTimestamp("fecha"));
                orden.setTotal(calcularTotalOrden(orden.getId()));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordenes;
    }
    
    public ArrayList<Orden> getOrdenesActivas() {
        ArrayList<Orden> ordenes = new ArrayList<>();
        try {
            String sql = "SELECT o.id, o.mesa_id, o.mesero, o.estado, o.fecha, m.capacidad " +
                        "FROM ordenes o JOIN mesas m ON o.mesa_id = m.numero " +
                        "WHERE o.estado = 'Pendiente' ORDER BY o.id DESC";
            Statement stmt = conexion.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("mesa_id"), rs.getInt("capacidad"));
                Orden orden = new Orden(rs.getInt("id"), mesa, rs.getString("mesero"));
                orden.setEstado(rs.getString("estado"));
                orden.setTotal(calcularTotalOrden(orden.getId()));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordenes;
    }
    
    private double calcularTotalOrden(int ordenId) {
        double total = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT SUM(d.cantidad * p.precio_venta) as total " +
                        "FROM detalle_orden d JOIN productos p ON d.id_producto = p.id " +
                        "WHERE d.id_orden = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, ordenId);
            rs = ps.executeQuery();
            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return total;
    }
    
    public String marcarOrdenPagada(int ordenId) {
        PreparedStatement psCheck = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        PreparedStatement psMesa = null;
        
        try {
            String sqlCheck = "SELECT estado, mesa_id FROM ordenes WHERE id = ?";
            psCheck = conexion.prepareStatement(sqlCheck);
            psCheck.setInt(1, ordenId);
            rs = psCheck.executeQuery();
            
            if (!rs.next()) {
                return "La orden no existe";
            }
            
            String estado = rs.getString("estado");
            int mesaId = rs.getInt("mesa_id");
            
            if ("Pagada".equals(estado)) {
                return "La orden ya está pagada";
            }
            
            if ("Cancelada".equals(estado)) {
                return "No se puede pagar una orden cancelada";
            }
            
            String sql = "UPDATE ordenes SET estado = 'Pagada' WHERE id = ?";
            ps = conexion.prepareStatement(sql);
            ps.setInt(1, ordenId);
            ps.executeUpdate();
            
            String sqlMesa = "UPDATE mesas SET estado = 'Libre', mesero_asignado = NULL WHERE numero = ?";
            psMesa = conexion.prepareStatement(sqlMesa);
            psMesa.setInt(1, mesaId);
            psMesa.executeUpdate();
            
            return "OK";
            
        } catch (SQLException e) {
            return "Error de base de datos: " + e.getMessage();
        } finally {
            try {
                if (rs != null) rs.close();
                if (psCheck != null) psCheck.close();
                if (ps != null) ps.close();
                if (psMesa != null) psMesa.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    
    public String cancelarOrden(int ordenId) {
        try {
            String sqlCheck = "SELECT estado, mesa_id FROM ordenes WHERE id = ?";
            PreparedStatement psCheck = conexion.prepareStatement(sqlCheck);
            psCheck.setInt(1, ordenId);
            ResultSet rs = psCheck.executeQuery();
            
            if (!rs.next()) {
                return "La orden no existe";
            }
            
            String estado = rs.getString("estado");
            int mesaId = rs.getInt("mesa_id");
            
            if ("Cancelada".equals(estado)) {
                return "La orden ya está cancelada";
            }
            
            if ("Pagada".equals(estado)) {
                return "No se puede cancelar una orden pagada";
            }
            
            String sql = "UPDATE ordenes SET estado = 'Cancelada' WHERE id = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, ordenId);
            ps.executeUpdate();
            
            String sqlMesa = "UPDATE mesas SET estado = 'Libre', mesero_asignado = NULL WHERE numero = ?";
            PreparedStatement psMesa = conexion.prepareStatement(sqlMesa);
            psMesa.setInt(1, mesaId);
            psMesa.executeUpdate();
            
            return "OK";
            
        } catch (SQLException e) {
            return "Error de base de datos: " + e.getMessage();
        }
    }
    
    public ArrayList<ItemOrden> getItemsDeOrden(int ordenId) {
        ArrayList<ItemOrden> items = new ArrayList<>();
        try {
            String sql = "SELECT d.*, p.nombre, p.descripcion, p.precio_venta, p.disponible " +
                        "FROM detalle_orden d JOIN productos p ON d.id_producto = p.id " +
                        "WHERE d.id_orden = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, ordenId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Producto p = new Producto(
                    rs.getInt("id_producto"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getDouble("precio_venta"),
                    rs.getInt("disponible") == 1
                );
                ItemOrden item = new ItemOrden(rs.getInt("id"), p, rs.getInt("cantidad"), rs.getString("notas"));
                items.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    
    // ==================== MÉTODOS PARA REPORTES (CORREGIDOS PARA SQLITE) ====================
    
    public ArrayList<Orden> getHistorialOrdenes() {
        // Verificar si la tabla ordenes existe
        if (!tablaExiste("ordenes")) {
            System.out.println("❌ La tabla 'ordenes' no existe en la base de datos");
            return new ArrayList<Orden>();
        }
        
        ArrayList<Orden> ordenes = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT o.id, o.mesa_id, o.mesero, o.estado, o.fecha, m.capacidad " +
                        "FROM ordenes o JOIN mesas m ON o.mesa_id = m.numero " +
                        "WHERE o.estado = 'Pagada' ORDER BY o.id DESC";
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("mesa_id"), rs.getInt("capacidad"));
                Orden orden = new Orden(rs.getInt("id"), mesa, rs.getString("mesero"));
                orden.setEstado(rs.getString("estado"));
                orden.setFecha(rs.getTimestamp("fecha"));
                orden.setTotal(calcularTotalOrden(orden.getId()));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            System.out.println("Error en getHistorialOrdenes: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return ordenes;
    }
    
    // Método auxiliar para verificar si una tabla existe
    private boolean tablaExiste(String nombreTabla) {
        try {
            ResultSet rs = conexion.getMetaData().getTables(null, null, nombreTabla, null);
            boolean existe = rs.next();
            rs.close();
            return existe;
        } catch (SQLException e) {
            System.out.println("Error al verificar tabla " + nombreTabla + ": " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Orden> getOrdenesParaReporte(String filtro) {
        // Verificar si la tabla ordenes existe
        if (!tablaExiste("ordenes")) {
            System.out.println("❌ La tabla 'ordenes' no existe en la base de datos");
            return new ArrayList<Orden>();
        }
        
        ArrayList<Orden> ordenes = new ArrayList<>();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            String sql = "SELECT o.id, o.mesa_id, o.mesero, o.estado, o.fecha, m.capacidad " +
                        "FROM ordenes o JOIN mesas m ON o.mesa_id = m.numero";
            
            // Para "Todas" mostramos todas las órdenes (pendientes, pagadas, canceladas)
            // Para filtros específicos solo mostramos pagadas
            if (!"Todas".equals(filtro)) {
                sql += " WHERE o.estado IN ('Pagada', 'Pendiente')";
                
                // Ajustes de funciones de fecha para SQLite
                if ("Hoy".equals(filtro)) {
                    sql += " AND date(o.fecha) = date('now','localtime')";
                } else if ("Esta semana".equals(filtro)) {
                    sql += " AND strftime('%W', o.fecha) = strftime('%W', 'now','localtime')";
                } else if ("Este mes".equals(filtro)) {
                    sql += " AND strftime('%m', o.fecha) = strftime('%m', 'now','localtime') " +
                           " AND strftime('%Y', o.fecha) = strftime('%Y', 'now','localtime')";
                }
            }
            
            sql += " ORDER BY o.fecha DESC";
            
            stmt = conexion.createStatement();
            rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("mesa_id"), rs.getInt("capacidad"));
                Orden orden = new Orden(rs.getInt("id"), mesa, rs.getString("mesero"));
                orden.setEstado(rs.getString("estado"));
                orden.setFecha(rs.getTimestamp("fecha"));
                orden.setTotal(calcularTotalOrden(orden.getId()));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            System.out.println("Error en getOrdenesParaReporte: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        return ordenes;
    }
}