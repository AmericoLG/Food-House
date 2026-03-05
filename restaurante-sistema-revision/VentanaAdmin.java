import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

public class VentanaAdmin extends JFrame {
    private SistemaRestaurante sistema;
    private JTable tablaProductos;
    private DefaultTableModel modeloTablaProductos;
    private JTable tablaUsuarios;
    private DefaultTableModel modeloTablaUsuarios;
    private JTable tablaMesas;
    private DefaultTableModel modeloTablaMesas;
    private JTable tablaReporte;
    private DefaultTableModel modeloTablaReporte;
    private JLabel lblTotalVentas;
    private MesaDAO mesaDAO;
    
    public VentanaAdmin(SistemaRestaurante sistema) {
        this.sistema = sistema;
        this.mesaDAO = new MesaDAO();
        inicializar();
    }
    
    private void inicializar() {
        setTitle("Food House - Administrador");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Cambiar para controlar el cierre
        setLocationRelativeTo(null);
        
        // IMPORTANTE: Forzar que los botones respeten colores personalizados
        // Esto sobrepasa el LookAndFeel del sistema que está causando el problema
        UIManager.put("Button.foreground", Color.BLACK);
        UIManager.put("Button.background", new Color(240, 240, 240));
        UIManager.put("Button.focus", new Color(200, 200, 200));
        SwingUtilities.updateComponentTreeUI(this);
        
        // Agregar listener para manejar el cierre
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cerrarSesion();
            }
        });
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Pestaña Productos
        tabbedPane.addTab("Productos", crearPanelProductos());
        
        // Pestaña Usuarios
        tabbedPane.addTab("Usuarios", crearPanelUsuarios());
        
        // Pestaña Mesas
        tabbedPane.addTab("Mesas", crearPanelMesas());
        
        // Pestaña Reportes
        tabbedPane.addTab("Reportes", crearPanelReportes());
        
        add(tabbedPane);
    }
    
    // ==================== PESTAÑA PRODUCTOS ====================
    
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Gestion de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(102, 126, 234));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnActualizar = new JButton("Actualizar");
        
        // Estilos de botones para productos
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnEditar.setBackground(new Color(102, 126, 234));
        btnEditar.setForeground(Color.BLACK);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnDesactivar.setBackground(new Color(255, 193, 7));
        btnDesactivar.setForeground(Color.BLACK);
        btnDesactivar.setFocusPainted(false);
        btnDesactivar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnActualizar.setBackground(new Color(108, 117, 125));
        btnActualizar.setForeground(Color.BLACK);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAgregar.addActionListener(e -> mostrarDialogoProducto(null));
        btnEditar.addActionListener(e -> editarProductoSeleccionado());
        btnDesactivar.addActionListener(e -> desactivarProductoSeleccionado());
        btnActualizar.addActionListener(e -> cargarProductos());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnDesactivar);
        panelBotones.add(btnActualizar);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        String[] columnas = {"ID", "Nombre", "Descripcion", "Precio", "Estado"};
        modeloTablaProductos = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaProductos = new JTable(modeloTablaProductos);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProductos.setRowHeight(30);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(new Color(102, 126, 234));
        tablaProductos.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tablaProductos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarProductos();
        
        return panel;
    }
    
    private void cargarProductos() {
        modeloTablaProductos.setRowCount(0);
        try {
            if (sistema.getConexion() == null) {
                System.out.println("Error: Sin conexion a BD");
                return;
            }
            
            String sql = "SELECT id, nombre, descripcion, precio_venta, disponible FROM productos ORDER BY id DESC";
            Statement stmt = sistema.getConexion().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("nombre");
                fila[2] = rs.getString("descripcion");
                fila[3] = "bs" + String.format("%.2f", rs.getDouble("precio_venta"));
                fila[4] = rs.getBoolean("disponible") ? "Activo" : "Inactivo";
                modeloTablaProductos.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar productos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void mostrarDialogoProducto(Integer idProducto) {
        boolean esEdicion = idProducto != null;
        JDialog dialogo = new JDialog(this, esEdicion ? "Editar Producto" : "Nuevo Producto", true);
        dialogo.setSize(400, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        
        JPanel panelForm = new JPanel(new GridLayout(4, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtNombre = new JTextField();
        JTextArea txtDescripcion = new JTextArea(3, 20);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        JTextField txtPrecio = new JTextField();
        JCheckBox chkDisponible = new JCheckBox("Disponible", true);
        
        if (esEdicion) {
            try {
                String sql = "SELECT * FROM productos WHERE id = ?";
                PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                ps.setInt(1, idProducto);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtNombre.setText(rs.getString("nombre"));
                    txtDescripcion.setText(rs.getString("descripcion"));
                    txtPrecio.setText(String.valueOf(rs.getDouble("precio_venta")));
                    chkDisponible.setSelected(rs.getBoolean("disponible"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Descripcion:"));
        panelForm.add(scrollDesc);
        panelForm.add(new JLabel("Precio:"));
        panelForm.add(txtPrecio);
        panelForm.add(new JLabel(""));
        panelForm.add(chkDisponible);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        // Estilos de botones para diálogo de productos
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGuardar.addActionListener(e -> {
            try {
                String nombre = txtNombre.getText().trim();
                String descripcion = txtDescripcion.getText().trim();
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                boolean disponible = chkDisponible.isSelected();
                
                // Validación del nombre
                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "El nombre es obligatorio");
                    txtNombre.requestFocus();
                    return;
                }
                
                if (nombre.length() > 100) {
                    JOptionPane.showMessageDialog(dialogo, "El nombre no puede tener más de 100 caracteres\nActualmente: " + nombre.length() + " caracteres");
                    txtNombre.requestFocus();
                    return;
                }
                
                // Validación del precio
                if (precio <= 0) {
                    JOptionPane.showMessageDialog(dialogo, "El precio debe ser un número positivo");
                    txtPrecio.requestFocus();
                    return;
                }
                
                if (precio > 1000) {
                    JOptionPane.showMessageDialog(dialogo, "El precio no puede ser mayor a bs1000\nPrecio ingresado: bs" + String.format("%.2f", precio));
                    txtPrecio.requestFocus();
                    return;
                }
                
                if (esEdicion) {
                    String sql = "UPDATE productos SET nombre=?, descripcion=?, precio_venta=?, disponible=? WHERE id=?";
                    PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                    ps.setString(1, nombre);
                    ps.setString(2, descripcion);
                    ps.setDouble(3, precio);
                    ps.setBoolean(4, disponible);
                    ps.setInt(5, idProducto);
                    ps.executeUpdate();
                } else {
                    String sql = "INSERT INTO productos (nombre, descripcion, precio_venta, disponible) VALUES (?, ?, ?, ?)";
                    PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                    ps.setString(1, nombre);
                    ps.setString(2, descripcion);
                    ps.setDouble(3, precio);
                    ps.setBoolean(4, disponible);
                    ps.executeUpdate();
                }
                
                cargarProductos();
                dialogo.dispose();
                JOptionPane.showMessageDialog(this, "Producto guardado correctamente");
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Precio inválido. Ingrese un número válido (ej: 150.50)");
                txtPrecio.requestFocus();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error inesperado: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelForm, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private void editarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto para editar");
            return;
        }
        int id = (Integer) modeloTablaProductos.getValueAt(fila, 0);
        mostrarDialogoProducto(id);
    }
    
    private void desactivarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un producto");
            return;
        }
        
        int id = (Integer) modeloTablaProductos.getValueAt(fila, 0);
        String estadoActual = (String) modeloTablaProductos.getValueAt(fila, 4);
        boolean nuevoEstado = !estadoActual.equals("Activo");
        String accion = nuevoEstado ? "activar" : "desactivar";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseas " + accion + " este producto?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "UPDATE productos SET disponible = ? WHERE id = ?";
                PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                ps.setBoolean(1, nuevoEstado);
                ps.setInt(2, id);
                ps.executeUpdate();
                cargarProductos();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ==================== PESTAÑA USUARIOS ====================
    
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Gestion de Usuarios");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(102, 126, 234));
        panelSuperior.add(lblTitulo, BorderLayout.WEST);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar = new JButton("Editar");
        JButton btnDesactivar = new JButton("Desactivar");
        JButton btnActualizar = new JButton("Actualizar");
        
        // Estilos de botones para usuarios
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnEditar.setBackground(new Color(102, 126, 234));
        btnEditar.setForeground(Color.BLACK);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnDesactivar.setBackground(new Color(255, 193, 7));
        btnDesactivar.setForeground(Color.BLACK);
        btnDesactivar.setFocusPainted(false);
        btnDesactivar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnActualizar.setBackground(new Color(108, 117, 125));
        btnActualizar.setForeground(Color.BLACK);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAgregar.addActionListener(e -> mostrarDialogoUsuario(null));
        btnEditar.addActionListener(e -> editarUsuarioSeleccionado());
        btnDesactivar.addActionListener(e -> desactivarUsuarioSeleccionado());
        btnActualizar.addActionListener(e -> cargarUsuarios());
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnDesactivar);
        panelBotones.add(btnActualizar);
        panelSuperior.add(panelBotones, BorderLayout.EAST);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        String[] columnas = {"ID", "Usuario", "Nombre", "Rol", "Estado"};
        modeloTablaUsuarios = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaUsuarios = new JTable(modeloTablaUsuarios);
        tablaUsuarios.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaUsuarios.setRowHeight(30);
        tablaUsuarios.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaUsuarios.getTableHeader().setBackground(new Color(102, 126, 234));
        tablaUsuarios.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tablaUsuarios);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        cargarUsuarios();
        
        return panel;
    }
    
    // ==================== PESTAÑA MESAS ====================
    
    private JPanel crearPanelMesas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(Color.WHITE);
        
        JLabel titulo = new JLabel("Gestión de Mesas");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titulo.setForeground(new Color(33, 37, 41));
        panelSuperior.add(titulo, BorderLayout.NORTH);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBotones.setBackground(Color.WHITE);
        
        JButton btnAgregar = new JButton("Nueva Mesa");
        btnAgregar.setBackground(new Color(40, 167, 69));
        btnAgregar.setForeground(Color.BLACK);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnEditar = new JButton("Editar Mesa");
        btnEditar.setBackground(new Color(102, 126, 234));
        btnEditar.setForeground(Color.BLACK);
        btnEditar.setFocusPainted(false);
        btnEditar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JButton btnEliminar = new JButton("Eliminar Mesa");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.BLACK);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelSuperior.add(panelBotones, BorderLayout.CENTER);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Tabla de mesas
        String[] columnas = {"Número", "Capacidad", "Estado"};
        modeloTablaMesas = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMesas = new JTable(modeloTablaMesas);
        tablaMesas.setRowHeight(25);
        tablaMesas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaMesas.getTableHeader().setBackground(new Color(102, 126, 234));
        tablaMesas.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane scrollPane = new JScrollPane(tablaMesas);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Eventos de botones
        btnAgregar.addActionListener(e -> mostrarDialogoMesa(null));
        btnEditar.addActionListener(e -> {
            int filaSeleccionada = tablaMesas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una mesa para editar");
                return;
            }
            int numeroMesa = (Integer) modeloTablaMesas.getValueAt(filaSeleccionada, 0);
            mostrarDialogoMesa(numeroMesa);
        });
        
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaMesas.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una mesa para eliminar");
                return;
            }
            
            int numeroMesa = (Integer) modeloTablaMesas.getValueAt(filaSeleccionada, 0);
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Está seguro de eliminar la mesa " + numeroMesa + "?", 
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                if (mesaDAO.eliminar(numeroMesa)) {
                    JOptionPane.showMessageDialog(this, "Mesa eliminada correctamente");
                    cargarMesas();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar la mesa");
                }
            }
        });
        
        cargarMesas();
        
        return panel;
    }
    
    private void cargarMesas() {
        modeloTablaMesas.setRowCount(0);
        ArrayList<Mesa> mesas = mesaDAO.obtenerTodas();
        
        for (Mesa m : mesas) {
            Object[] fila = new Object[3];
            fila[0] = m.getNumero();
            fila[1] = m.getCapacidad();
            fila[2] = m.getEstado();
            modeloTablaMesas.addRow(fila);
        }
    }
    
    private void mostrarDialogoMesa(Integer numeroMesa) {
        boolean esEdicion = numeroMesa != null;
        JDialog dialogo = new JDialog(this, esEdicion ? "Editar Mesa" : "Nueva Mesa", true);
        dialogo.setSize(350, 200);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        
        JPanel panelForm = new JPanel(new GridLayout(2, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtNumero = new JTextField();
        
        if (esEdicion) {
            Mesa mesa = mesaDAO.obtenerPorNumero(numeroMesa);
            if (mesa != null) {
                txtNumero.setText(String.valueOf(mesa.getNumero()));
                txtNumero.setEnabled(false); // No permitir cambiar el número en edición
            }
        }
        
        panelForm.add(new JLabel("Número de Mesa:"));
        panelForm.add(txtNumero);
        panelForm.add(new JLabel("Capacidad (fija):"));
        panelForm.add(new JLabel("4 personas"));
        
        JPanel panelBotones = new JPanel(new FlowLayout());
        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.BLACK);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.BLACK);
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelForm, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        
        btnGuardar.addActionListener(e -> {
            try {
                int numero = Integer.parseInt(txtNumero.getText().trim());
                
                if (numero <= 0) {
                    JOptionPane.showMessageDialog(dialogo, "El número de mesa debe ser mayor a 0");
                    txtNumero.requestFocus();
                    return;
                }
                
                // Validación de número duplicado (solo para nuevas mesas)
                if (!esEdicion) {
                    Mesa mesaExistente = mesaDAO.obtenerPorNumero(numero);
                    if (mesaExistente != null) {
                        JOptionPane.showMessageDialog(dialogo, "Ya existe una mesa con el número " + numero + "\nPor favor, use otro número");
                        txtNumero.requestFocus();
                        txtNumero.selectAll();
                        return;
                    }
                }
                
                // Crear mesa con valores por defecto
                Mesa mesa = new Mesa(numero, 4); // Capacidad fija de 4
                mesa.setEstado("Libre"); // Estado por defecto
                mesa.setMeseroAsignado(""); // Sin mesero asignado
                mesa.setReservada(false); // No reservada por defecto
                mesa.setReservadaPara(""); // Sin reserva
                
                boolean exito;
                if (esEdicion) {
                    exito = mesaDAO.actualizar(mesa);
                } else {
                    exito = mesaDAO.insertar(mesa);
                }
                
                if (exito) {
                    cargarMesas();
                    dialogo.dispose();
                    JOptionPane.showMessageDialog(this, "Mesa guardada correctamente");
                } else {
                    JOptionPane.showMessageDialog(dialogo, "Error al guardar la mesa");
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese un número válido");
                txtNumero.requestFocus();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        dialogo.setVisible(true);
    }
    
    private void cargarUsuarios() {
        modeloTablaUsuarios.setRowCount(0);
        try {
            String sql = "SELECT id, usuario, rol FROM usuarios ORDER BY id DESC";
            Statement stmt = sistema.getConexion().createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Object[] fila = new Object[5];
                fila[0] = rs.getInt("id");
                fila[1] = rs.getString("usuario");
                fila[2] = rs.getString("rol");
                fila[3] = "Activo";
                fila[4] = "Activo";
                modeloTablaUsuarios.addRow(fila);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar usuarios: " + e.getMessage());
        }
    }
    
    private void mostrarDialogoUsuario(Integer idUsuario) {
        boolean esEdicion = idUsuario != null;
        JDialog dialogo = new JDialog(this, esEdicion ? "Editar Usuario" : "Nuevo Usuario", true);
        dialogo.setSize(400, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout(10, 10));
        
        JPanel panelForm = new JPanel(new GridLayout(5, 2, 10, 10));
        panelForm.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTextField txtUsername = new JTextField();
        JPasswordField txtPassword = new JPasswordField();
        JTextField txtNombre = new JTextField();
        String[] roles = {"Administrador", "Mesero", "Cocinero"};
        JComboBox<String> comboRol = new JComboBox<String>(roles);
        JCheckBox chkActivo = new JCheckBox("Activo", true);
        
        if (esEdicion) {
            try {
                String sql = "SELECT * FROM usuarios WHERE id = ?";
                PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                ps.setInt(1, idUsuario);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtUsername.setText(rs.getString("usuario"));
                    txtNombre.setText(rs.getString("nombre"));
                    comboRol.setSelectedItem(rs.getString("rol"));
                    chkActivo.setSelected(rs.getBoolean("activo"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        panelForm.add(new JLabel("Usuario:"));
        panelForm.add(txtUsername);
        panelForm.add(new JLabel("Password:"));
        panelForm.add(txtPassword);
        panelForm.add(new JLabel("Nombre:"));
        panelForm.add(txtNombre);
        panelForm.add(new JLabel("Rol:"));
        panelForm.add(comboRol);
        panelForm.add(new JLabel(""));
        panelForm.add(chkActivo);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        
        // Estilos de botones para diálogo de usuarios
        btnGuardar.setBackground(new Color(40, 167, 69));
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCancelar.setBackground(new Color(108, 117, 125));
        btnCancelar.setForeground(Color.BLACK);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGuardar.addActionListener(e -> {
            try {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());
                String nombre = txtNombre.getText().trim();
                String rol = (String) comboRol.getSelectedItem();
                boolean activo = chkActivo.isSelected();
                
                if (username.isEmpty() || nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "Usuario y nombre son obligatorios");
                    return;
                }
                
                if (!esEdicion && password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "La contraseña es obligatoria para nuevos usuarios");
                    return;
                }
                
                if (esEdicion) {
                    String sql = "UPDATE usuarios SET usuario=?, rol=? WHERE id=?";
                    PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                    ps.setString(1, username);
                    ps.setString(2, rol);
                    ps.setInt(3, idUsuario);
                    ps.executeUpdate();
                    
                    if (!password.isEmpty()) {
                        String sqlPass = "UPDATE usuarios SET password=? WHERE id=?";
                        PreparedStatement psPass = sistema.getConexion().prepareStatement(sqlPass);
                        psPass.setString(1, password);
                        psPass.setInt(2, idUsuario);
                        psPass.executeUpdate();
                    }
                } else {
                    String sql = "INSERT INTO usuarios (usuario, password, rol) VALUES (?, ?, ?)";
                    PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                    ps.setString(1, username);
                    ps.setString(2, password);
                    ps.setString(3, rol);
                    ps.executeUpdate();
                }
                
                cargarUsuarios();
                dialogo.dispose();
                JOptionPane.showMessageDialog(this, "Usuario guardado correctamente");
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialogo, "Error: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> dialogo.dispose());
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        dialogo.add(panelForm, BorderLayout.CENTER);
        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }
    
    private void editarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario para editar");
            return;
        }
        int id = (Integer) modeloTablaUsuarios.getValueAt(fila, 0);
        mostrarDialogoUsuario(id);
    }
    
    private void desactivarUsuarioSeleccionado() {
        int fila = tablaUsuarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un usuario");
            return;
        }
        
        int id = (Integer) modeloTablaUsuarios.getValueAt(fila, 0);
        String username = (String) modeloTablaUsuarios.getValueAt(fila, 1);
        
        if (username.equals(sistema.getUsuarioActual().getUsuario())) {
            JOptionPane.showMessageDialog(this, "No puedes desactivar tu propio usuario");
            return;
        }
        
        String estadoActual = (String) modeloTablaUsuarios.getValueAt(fila, 4);
        boolean nuevoEstado = !estadoActual.equals("Activo");
        String accion = nuevoEstado ? "activar" : "desactivar";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Deseas " + accion + " este usuario?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                String sql = "UPDATE usuarios SET activo = ? WHERE id = ?";
                PreparedStatement ps = sistema.getConexion().prepareStatement(sql);
                ps.setBoolean(1, nuevoEstado);
                ps.setInt(2, id);
                ps.executeUpdate();
                cargarUsuarios();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    // ==================== PESTAÑA REPORTES ====================
    
    private JPanel crearPanelReportes() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(Color.WHITE);
        
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelSuperior.setBackground(Color.WHITE);
        
        JLabel lblTitulo = new JLabel("Reporte de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(new Color(102, 126, 234));
        
        String[] filtros = {"Todas", "Hoy", "Esta semana", "Este mes"};
        JComboBox<String> comboFiltro = new JComboBox<String>(filtros);
        JButton btnGenerar = new JButton("Generar");
        JButton btnActualizar = new JButton("Actualizar");
        
        btnGenerar.addActionListener(e -> generarReporte((String) comboFiltro.getSelectedItem()));
        btnActualizar.addActionListener(e -> generarReporte("Todas"));
        
        panelSuperior.add(lblTitulo);
        panelSuperior.add(new JLabel("  Filtro:"));
        panelSuperior.add(comboFiltro);
        panelSuperior.add(btnGenerar);
        panelSuperior.add(btnActualizar);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        String[] columnas = {"ID Orden", "Mesa", "Mesero", "Fecha", "Total", "Estado"};
        modeloTablaReporte = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaReporte = new JTable(modeloTablaReporte);
        tablaReporte.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaReporte.setRowHeight(30);
        tablaReporte.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaReporte.getTableHeader().setBackground(new Color(102, 126, 234));
        tablaReporte.getTableHeader().setForeground(Color.BLACK);
        
        // Colores según estado
        tablaReporte.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String estado = (String) table.getValueAt(row, 5);
                if (!isSelected) {
                    switch (estado) {
                        case "Pagada":
                            c.setBackground(new Color(212, 237, 218));
                            c.setForeground(new Color(21, 87, 36));
                            break;
                        case "Pendiente":
                            c.setBackground(new Color(255, 243, 205));
                            c.setForeground(new Color(133, 100, 4));
                            break;
                        case "Cancelada":
                            c.setBackground(new Color(248, 215, 218));
                            c.setForeground(new Color(132, 53, 56));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tablaReporte);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInferior.setBackground(Color.WHITE);
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        lblTotalVentas = new JLabel("Total de ventas: bs0.00");
        lblTotalVentas.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotalVentas.setForeground(new Color(40, 167, 69));
        panelInferior.add(lblTotalVentas);
        
        panel.add(panelInferior, BorderLayout.SOUTH);
        
        generarReporte("Todas");
        
        return panel;
    }
    
    private void generarReporte(String filtro) {
        modeloTablaReporte.setRowCount(0);
        double totalVentas = 0.0;
        
        java.util.ArrayList<Orden> ordenes = null;
        
        try {
            // Para "Todas" mostramos todas las órdenes sin importar el estado
            if ("Todas".equals(filtro)) {
                ordenes = sistema.getOrdenesParaReporte("Todas");
            } else {
                ordenes = sistema.getOrdenesParaReporte(filtro);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ordenes = new java.util.ArrayList<Orden>();
        }
        
        if (ordenes == null) {
            ordenes = new java.util.ArrayList<Orden>();
        }
        
        for (int i = 0; i < ordenes.size(); i++) {
            Orden o = (Orden) ordenes.get(i);
            if (o == null) {
                continue;
            }
            
            double total = o.getTotal();
            totalVentas = totalVentas + total;
            
            String numeroMesa = "N/A";
            Mesa mesa = o.getMesa();
            if (mesa != null) {
                numeroMesa = Integer.toString(mesa.getNumero());
            }
            
            String mesero = o.getMesero();
            if (mesero == null) {
                mesero = "N/A";
            }
            
            String fecha = "N/A";
            if (o.getFecha() != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                fecha = sdf.format(o.getFecha());
            }
            
            String estado = o.getEstado();
            if (estado == null) {
                estado = "N/A";
            }
            
            Object[] fila = new Object[6];
            fila[0] = o.getId();
            fila[1] = "Mesa " + numeroMesa;
            fila[2] = mesero;
            fila[3] = fecha;
            fila[4] = "bs" + String.format("%.2f", total);
            fila[5] = estado;
            
            modeloTablaReporte.addRow(fila);
        }
        
        lblTotalVentas.setText("Total de ventas: bs" + String.format("%.2f", totalVentas));
    }
    
    // ==================== MÉTODO PARA CERRAR SESIÓN ====================
    
    private void cerrarSesion() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Está seguro de cerrar sesión?", 
            "Cerrar Sesión", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Cerrar sesión del sistema
            sistema.logout();
            
            // Cerrar ventana actual
            dispose();
            
            // Abrir ventana de login
            SwingUtilities.invokeLater(() -> {
                new VentanaLogin().setVisible(true);
            });
        }
    }
}