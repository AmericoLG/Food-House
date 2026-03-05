import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class VentanaCocina extends JFrame {
    private SistemaRestaurante sistema;
    private JTable tablaOrdenes;
    private DefaultTableModel modeloTabla;
    private Timer timerActualizacion;
    
    public VentanaCocina(SistemaRestaurante sistema) {
        this.sistema = sistema;
        inicializar();
        iniciarActualizacionAutomatica();
    }
    
    private void inicializar() {
        if (sistema == null || sistema.getUsuarioActual() == null) {
            JOptionPane.showMessageDialog(null, "Error: No hay sesión activa");
            dispose();
            return;
        }
        
        setTitle("Food House - Cocina");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBackground(new Color(44, 62, 80));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel panelHeader = new JPanel(new BorderLayout());
        panelHeader.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("ORDENES PENDIENTES", SwingConstants.LEFT);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitulo.setForeground(Color.WHITE);
        
        JLabel lblReloj = new JLabel();
        lblReloj.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblReloj.setForeground(new Color(241, 196, 15));
        
        // Actualizar reloj
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> 
                    lblReloj.setText(new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date()))
                );
            }
        }, 0, 1000);
        
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelInfo.setOpaque(false);
        JLabel lblUsuario = new JLabel("Cocinero: " + sistema.getUsuarioActual().getUsuario());
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUsuario.setForeground(Color.WHITE);
        panelInfo.add(lblUsuario);
        
        panelHeader.add(lblTitulo, BorderLayout.WEST);
        panelHeader.add(lblReloj, BorderLayout.CENTER);
        panelHeader.add(panelInfo, BorderLayout.EAST);
        
        panelPrincipal.add(panelHeader, BorderLayout.NORTH);
        
        // Tabla de órdenes
        String[] columnas = {"ID", "Mesa", "Mesero", "Producto", "Cant", "Notas", "Estado", "Acción"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Solo columna de acción
            }
        };
        
        tablaOrdenes = new JTable(modeloTabla);
        tablaOrdenes.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablaOrdenes.setRowHeight(50);
        tablaOrdenes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablaOrdenes.getTableHeader().setBackground(new Color(231, 76, 60));
        tablaOrdenes.getTableHeader().setForeground(Color.WHITE);
        
        // Colores según estado
        tablaOrdenes.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String estado = (String) table.getValueAt(row, 6);
                if (!isSelected) {
                    switch (estado) {
                        case "Pendiente":
                            c.setBackground(new Color(255, 243, 205));
                            c.setForeground(new Color(133, 100, 4));
                            break;
                        case "En espera":
                            c.setBackground(new Color(232, 232, 232));
                            c.setForeground(Color.BLACK);
                            break;
                        case "Preparando":
                            c.setBackground(new Color(204, 229, 255));
                            c.setForeground(new Color(0, 64, 133));
                            break;
                        case "Listo":
                            c.setBackground(new Color(212, 237, 218));
                            c.setForeground(new Color(21, 87, 36));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                return c;
            }
        });
        
        // Botones en tabla
        tablaOrdenes.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        tablaOrdenes.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane scrollPane = new JScrollPane(tablaOrdenes);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de leyenda
        JPanel panelLeyenda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLeyenda.setOpaque(false);
        panelLeyenda.add(crearLeyenda("⏳ Pendiente", new Color(255, 243, 205)));
        panelLeyenda.add(crearLeyenda("🕐 En espera", new Color(232, 232, 232)));
        panelLeyenda.add(crearLeyenda("🔥 Preparando", new Color(204, 229, 255)));
        panelLeyenda.add(crearLeyenda("✅ Listo", new Color(212, 237, 218)));
        
        panelPrincipal.add(panelLeyenda, BorderLayout.SOUTH);
        
        add(panelPrincipal);
        
        cargarOrdenes();
    }
    
    private JPanel crearLeyenda(String texto, Color color) {
        JPanel panel = new JPanel();
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JLabel lbl = new JLabel("  " + texto + "  ");
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lbl);
        return panel;
    }
    
    private void iniciarActualizacionAutomatica() {
        timerActualizacion = new Timer();
        timerActualizacion.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> cargarOrdenes());
            }
        }, 0, 5000); // Actualizar cada 5 segundos
    }
    
    private void cargarOrdenes() {
        modeloTabla.setRowCount(0);
        
        try {
            ArrayList<ItemOrden> items = sistema.getItemsPendientesCocina();
            
            for (ItemOrden item : items) {
                String botonTexto = getTextoBoton(item.getEstado());
                
                Object[] fila = {
                    item.getId(),
                    "Mesa " + item.getMesaId(),
                    item.getMesero() != null ? item.getMesero() : "N/A",
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getNotas() != null ? item.getNotas() : "-",
                    item.getEstado(),
                    botonTexto
                };
                modeloTabla.addRow(fila);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private String getTextoBoton(String estado) {
        switch (estado) {
            case "Pendiente": return "→ En espera";
            case "En espera": return "→ Preparando";
            case "Preparando": return "→ Listo";
            case "Listo": return "→ Entregado";
            default: return "";
        }
    }
    
    private String getSiguienteEstado(String estadoActual) {
        switch (estadoActual) {
            case "Pendiente": return "En espera";
            case "En espera": return "Preparando";
            case "Preparando": return "Listo";
            case "Listo": return "Entregado";
            default: return estadoActual;
        }
    }
    
    private void cambiarEstado(int itemId, String estadoActual) {
        String nuevoEstado = getSiguienteEstado(estadoActual);
        
        if (sistema.actualizarEstadoItemCocina(itemId, nuevoEstado)) {
            if (nuevoEstado.equals("Listo")) {
                Toolkit.getDefaultToolkit().beep();
            }
            cargarOrdenes();
        } else {
            JOptionPane.showMessageDialog(this, "Error al actualizar estado");
        }
    }
    
    // Renderizador de botones
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setForeground(Color.BLACK); // ✅ NEGRO
        }
        
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setForeground(Color.BLACK); // ✅ NEGRO
            
            String estado = (String) table.getValueAt(row, 6);
            switch (estado) {
                case "Pendiente":
                    setBackground(new Color(255, 193, 7));
                    break;
                case "En espera":
                    setBackground(new Color(108, 117, 125));
                    setForeground(Color.WHITE);
                    break;
                case "Preparando":
                    setBackground(new Color(0, 123, 255));
                    setForeground(Color.WHITE);
                    break;
                case "Listo":
                    setBackground(new Color(40, 167, 69));
                    setForeground(Color.WHITE);
                    break;
                default:
                    setBackground(Color.LIGHT_GRAY);
            }
            return this;
        }
    }
    
    // Editor de botones
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.BLACK); // ✅ NEGRO
            button.addActionListener(e -> fireEditingStopped());
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setForeground(Color.BLACK); // ✅ NEGRO
            currentRow = row;
            
            String estado = (String) table.getValueAt(row, 6);
            switch (estado) {
                case "Pendiente":
                    button.setBackground(new Color(255, 193, 7));
                    break;
                case "En espera":
                    button.setBackground(new Color(108, 117, 125));
                    button.setForeground(Color.WHITE);
                    break;
                case "Preparando":
                    button.setBackground(new Color(0, 123, 255));
                    button.setForeground(Color.WHITE);
                    break;
                case "Listo":
                    button.setBackground(new Color(40, 167, 69));
                    button.setForeground(Color.WHITE);
                    break;
            }
            
            isPushed = true;
            return button;
        }
        
        public Object getCellEditorValue() {
            if (isPushed) {
                int itemId = (int) tablaOrdenes.getValueAt(currentRow, 0);
                String estado = (String) tablaOrdenes.getValueAt(currentRow, 6);
                cambiarEstado(itemId, estado);
            }
            isPushed = false;
            return label;
        }
        
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
    
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
    
    @Override
    public void dispose() {
        if (timerActualizacion != null) {
            timerActualizacion.cancel();
        }
        super.dispose();
    }
}