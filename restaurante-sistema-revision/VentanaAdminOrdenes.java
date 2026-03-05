import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

// Asumiendo que estas clases existen en tu proyecto:
// import modelo.Orden;
// import modelo.ItemOrden;
// import modelo.Producto;
// import modelo.Mesa;

public class VentanaAdminOrdenes extends JFrame {
    private SistemaRestaurante sistema;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;
    private JLabel lblInfo;
    
    public VentanaAdminOrdenes(SistemaRestaurante sistema) {
        this.sistema = sistema;
        inicializar();
    }
    
    private void inicializar() {
        // Verificar que hay usuario logueado
        if (sistema == null || sistema.getUsuarioActual() == null) {
            JOptionPane.showMessageDialog(null, "Error: No hay sesión activa");
            dispose();
            return;
        }
        
        setTitle("Administración de Órdenes - " + sistema.getUsuarioActual().getUsuario());
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(40, 167, 69));
        header.setPreferredSize(new Dimension(0, 50));
        
        JLabel lblTitulo = new JLabel("ADMINISTRACIÓN DE ÓRDENES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        
        panel.add(header, BorderLayout.NORTH);
        
        // Panel de filtros
        JPanel panelFiltros = new JPanel();
        panelFiltros.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<String> comboFiltro = new JComboBox<>(new String[]{"Todas", "Pendientes", "Pagadas", "Canceladas"});
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnActualizar = new JButton("Actualizar");
        
        btnFiltrar.setBackground(new Color(0, 123, 255));
        btnFiltrar.setForeground(Color.BLACK);
        
        btnActualizar.setBackground(new Color(40, 167, 69));
        btnActualizar.setForeground(Color.BLACK);
        
        panelFiltros.add(new JLabel("Filtrar por:"));
        panelFiltros.add(comboFiltro);
        panelFiltros.add(btnFiltrar);
        panelFiltros.add(btnActualizar);
        
        // Panel norte combinado
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(header, BorderLayout.NORTH);
        panelNorte.add(panelFiltros, BorderLayout.SOUTH);
        
        panel.add(panelNorte, BorderLayout.NORTH);
        
        // Tabla de órdenes
        String[] columnas = {"ID", "Mesa", "Mesero", "Total", "Estado", "Fecha"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scroll = new JScrollPane(tablaOrdenes);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Panel de botones de acción
        JPanel panelBotones = new JPanel();
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnVerDetalle = new JButton("Ver Detalle");
        JButton btnMarcarPagada = new JButton("Marcar Pagada");
        JButton btnCancelar = new JButton("Cancelar Orden");
        JButton btnCerrar = new JButton("Cerrar");
        
        btnVerDetalle.setBackground(new Color(0, 123, 255));
        btnVerDetalle.setForeground(Color.BLACK);
        
        btnMarcarPagada.setBackground(new Color(40, 167, 69));
        btnMarcarPagada.setForeground(Color.BLACK);
        
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.BLACK);
        
        btnCerrar.setBackground(new Color(108, 117, 125));
        btnCerrar.setForeground(Color.BLACK);
        
        panelBotones.add(btnVerDetalle);
        panelBotones.add(btnMarcarPagada);
        panelBotones.add(btnCancelar);
        panelBotones.add(btnCerrar);
        
        // Info
        lblInfo = new JLabel("Selecciona una orden para ver opciones");
        panelBotones.add(lblInfo);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        add(panel);
        
        // Cargar datos iniciales
        cargarTodasLasOrdenes();
        
        // Acciones
        btnFiltrar.addActionListener(e -> {
            String filtro = (String) comboFiltro.getSelectedItem();
            filtrarOrdenes(filtro);
        });
        
        btnActualizar.addActionListener(e -> cargarTodasLasOrdenes());
        
        btnVerDetalle.addActionListener(e -> {
            int fila = tablaOrdenes.getSelectedRow();
            if (fila >= 0) {
                int ordenId = (int) modeloTabla.getValueAt(fila, 0);
                mostrarDetalleOrden(ordenId);
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una orden", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Marcar pagada
        btnMarcarPagada.addActionListener(e -> {
            int fila = tablaOrdenes.getSelectedRow();
            if (fila >= 0) {
                int ordenId = (int) modeloTabla.getValueAt(fila, 0);
                String estado = (String) modeloTabla.getValueAt(fila, 4);
                
                if ("Pagada".equals(estado)) {
                    JOptionPane.showMessageDialog(this, "La orden ya está pagada", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Marcar orden " + ordenId + " como PAGADA?", 
                    "Confirmar", JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Verificar que el método existe en SistemaRestaurante
                    try {
                        String resultado = sistema.marcarOrdenPagada(ordenId);
                        
                        if ("OK".equals(resultado)) {
                            JOptionPane.showMessageDialog(this, "Orden marcada como pagada");
                            cargarTodasLasOrdenes();
                        } else {
                            JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una orden", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Cancelar orden
        btnCancelar.addActionListener(e -> {
            int fila = tablaOrdenes.getSelectedRow();
            if (fila >= 0) {
                int ordenId = (int) modeloTabla.getValueAt(fila, 0);
                String estado = (String) modeloTabla.getValueAt(fila, 4);
                
                if ("Cancelada".equals(estado)) {
                    JOptionPane.showMessageDialog(this, "La orden ya está cancelada", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿CANCELAR la orden " + ordenId + "?\nEsta acción no se puede deshacer.", 
                    "Confirmar Cancelación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        String resultado = sistema.cancelarOrden(ordenId);
                        
                        if ("OK".equals(resultado)) {
                            JOptionPane.showMessageDialog(this, "Orden cancelada");
                            cargarTodasLasOrdenes();
                        } else {
                            JOptionPane.showMessageDialog(this, resultado, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Selecciona una orden", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnCerrar.addActionListener(e -> this.dispose());
    }
    
    private void cargarTodasLasOrdenes() {
        modeloTabla.setRowCount(0);
        try {
            ArrayList<Orden> ordenes = sistema.getTodasLasOrdenes();
            if (ordenes != null) {
                for (Orden o : ordenes) {
                    if (o != null) {
                        modeloTabla.addRow(new Object[]{
                            o.getId(),
                            o.getMesa() != null ? o.getMesa().getNumero() : "N/A",
                            o.getMesero(),
                            "bs" + String.format("%.2f", o.getTotal()),
                            o.getEstado(),
                            new java.util.Date().toString()
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar órdenes: " + e.getMessage());
        }
    }
    
    private void filtrarOrdenes(String filtro) {
        modeloTabla.setRowCount(0);
        ArrayList<Orden> ordenes = new ArrayList<>();
        
        try {
            switch (filtro) {
                case "Pendientes":
                    ordenes = sistema.getOrdenesActivas();
                    break;
                case "Pagadas":
                    ordenes = sistema.getHistorialOrdenes();
                    break;
                case "Canceladas":
                    ordenes = sistema.getTodasLasOrdenes();
                    if (ordenes != null) {
                        ordenes.removeIf(o -> o != null && !"Cancelada".equals(o.getEstado()));
                    }
                    break;
                default:
                    ordenes = sistema.getTodasLasOrdenes();
            }
            
            if (ordenes != null) {
                for (Orden o : ordenes) {
                    if (o != null) {
                        modeloTabla.addRow(new Object[]{
                            o.getId(),
                            o.getMesa() != null ? o.getMesa().getNumero() : "N/A",
                            o.getMesero(),
                            "bs" + String.format("%.2f", o.getTotal()),
                            o.getEstado(),
                            new java.util.Date().toString()
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al filtrar: " + e.getMessage());
        }
    }
    
    private void mostrarDetalleOrden(int ordenId) {
        JDialog dialog = new JDialog(this, "Detalle de Orden " + ordenId, true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Tabla de items
        String[] cols = {"Producto", "Cantidad", "Precio", "Subtotal"};
        DefaultTableModel modeloItems = new DefaultTableModel(cols, 0);
        
        try {
            ArrayList<ItemOrden> items = sistema.getItemsDeOrden(ordenId);
            if (items != null) {
                for (ItemOrden item : items) {
                    if (item != null && item.getProducto() != null) {
                        modeloItems.addRow(new Object[]{
                            item.getProducto().getNombre(),
                            item.getCantidad(),
                            "bs" + String.format("%.2f", item.getProducto().getPrecio()),
                            "bs" + String.format("%.2f", item.getSubtotal())
                        });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, "Error al cargar items: " + e.getMessage());
        }
        
        JTable tablaItems = new JTable(modeloItems);
        panel.add(new JScrollPane(tablaItems), BorderLayout.CENTER);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setForeground(Color.BLACK);
        btnCerrar.addActionListener(e -> dialog.dispose());
        
        JPanel panelSur = new JPanel();
        panelSur.add(btnCerrar);
        panel.add(panelSur, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
}