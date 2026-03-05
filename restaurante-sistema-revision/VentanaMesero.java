import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class VentanaMesero extends JFrame {
    private SistemaRestaurante sistema;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal;
    private JComboBox<Producto> comboProd;
    private JComboBox<Mesa> comboMesas;
    private JSpinner spinner;
    
    public VentanaMesero(SistemaRestaurante sistema) {
        this.sistema = sistema;
        inicializar();
    }
    
    private void inicializar() {
        if (sistema == null || sistema.getUsuarioActual() == null) {
            JOptionPane.showMessageDialog(null, "Error: No hay sesión activa");
            dispose();
            return;
        }
        
        setTitle("Mesero - " + sistema.getUsuarioActual().getUsuario());
        setSize(900, 600);
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
        
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(40, 167, 69));
        header.setPreferredSize(new Dimension(0, 50));
        
        JLabel lblTitulo = new JLabel("NUEVA ORDEN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        header.add(lblTitulo);
        
        panel.add(header, BorderLayout.NORTH);
        
        // Panel izquierdo
        JPanel panelIzq = new JPanel(new GridLayout(4, 1, 10, 10));
        panelIzq.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelIzq.setPreferredSize(new Dimension(300, 0));
        
        // Selector mesa
        JPanel pMesa = new JPanel(new BorderLayout());
        pMesa.setBorder(BorderFactory.createTitledBorder("Mesa"));
        comboMesas = new JComboBox<>();
        
        // Personalizar renderer para mostrar "Mesa N" en lugar del objeto
        comboMesas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                    boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Mesa) {
                    Mesa mesa = (Mesa) value;
                    label.setText("Mesa " + mesa.getNumero());
                }
                return label;
            }
        });
        
        try {
            ArrayList<Mesa> mesas = sistema.getMesasDisponibles();
            if (mesas != null && !mesas.isEmpty()) {
                for (Mesa m : mesas) {
                    if (m != null && !m.isReservada()) comboMesas.addItem(m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        pMesa.add(comboMesas);
        panelIzq.add(pMesa);
        
        // Selector producto
        JPanel pProd = new JPanel(new BorderLayout());
        pProd.setBorder(BorderFactory.createTitledBorder("Producto"));
        comboProd = new JComboBox<>();
        
        try {
            ArrayList<Producto> menu = sistema.getMenu();
            if (menu != null && !menu.isEmpty()) {
                for (Producto p : menu) {
                    if (p != null) comboProd.addItem(p);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        pProd.add(comboProd);
        panelIzq.add(pProd);
        
        // Cantidad
        JPanel pCant = new JPanel(new BorderLayout());
        pCant.setBorder(BorderFactory.createTitledBorder("Cantidad"));
        SpinnerNumberModel model = new SpinnerNumberModel(1, 1, 20, 1);
        spinner = new JSpinner(model);
        pCant.add(spinner);
        panelIzq.add(pCant);
        
        // Botón agregar - TEXTO NEGRO
        JButton btnAgregar = new JButton("AGREGAR A ORDEN");
        btnAgregar.setBackground(new Color(0, 123, 255));
        btnAgregar.setForeground(Color.BLACK); // ✅ NEGRO
        btnAgregar.setFont(new Font("Arial", Font.BOLD, 14));
        panelIzq.add(btnAgregar);
        
        panel.add(panelIzq, BorderLayout.WEST);
        
        // Tabla
        String[] cols = {"Producto", "Cant", "Precio", "Subtotal"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabla = new JTable(modeloTabla);
        JScrollPane scroll = new JScrollPane(tabla);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Panel inferior
        JPanel panelSur = new JPanel(new BorderLayout());
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        lblTotal = new JLabel("Total: bs0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 24));
        panelSur.add(lblTotal, BorderLayout.WEST);
        
        JPanel pBotones = new JPanel();
        
        // Botón Finalizar - TEXTO NEGRO
        JButton btnFinalizar = new JButton("Finalizar");
        btnFinalizar.setBackground(new Color(40, 167, 69));
        btnFinalizar.setForeground(Color.BLACK); // ✅ NEGRO
        
        // Botón Cancelar - TEXTO NEGRO
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setBackground(new Color(220, 53, 69));
        btnCancelar.setForeground(Color.BLACK); // ✅ NEGRO
        
        pBotones.add(btnFinalizar);
        pBotones.add(btnCancelar);
        panelSur.add(pBotones, BorderLayout.EAST);
        
        panel.add(panelSur, BorderLayout.SOUTH);
        
        add(panel);
        
        // Acciones
        btnAgregar.addActionListener(e -> {
            if (comboMesas.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una mesa", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Mesa m = (Mesa) comboMesas.getSelectedItem();
            
            if (sistema.getOrdenActual() == null) {
                try {
                    Orden orden = sistema.crearOrden(m.getNumero());
                    if (orden == null) {
                        JOptionPane.showMessageDialog(this, "Error al crear orden", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            if (comboProd.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un producto", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Producto p = (Producto) comboProd.getSelectedItem();
            int cant = (Integer) spinner.getValue();
            
            try {
                sistema.agregarItemAOrden(p, cant, "");
                actualizarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al agregar item: " + ex.getMessage());
            }
        });
        
        btnFinalizar.addActionListener(e -> {
            if (sistema.getOrdenActual() == null) {
                JOptionPane.showMessageDialog(this, "No hay orden para finalizar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                sistema.finalizarOrden();
                JOptionPane.showMessageDialog(this, "Orden enviada a cocina");
                modeloTabla.setRowCount(0);
                actualizarTotal();
                comboMesas.setEnabled(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al finalizar: " + ex.getMessage());
            }
        });
        
        btnCancelar.addActionListener(e -> {
            sistema.logout();
            new VentanaLogin().setVisible(true);
            this.dispose();
        });
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        try {
            Orden ordenActual = sistema.getOrdenActual();
            if (ordenActual != null && ordenActual.getItems() != null) {
                for (ItemOrden item : ordenActual.getItems()) {
                    if (item != null && item.getProducto() != null) {
                        modeloTabla.addRow(new Object[]{
                            item.getProducto().getNombre(),
                            item.getCantidad(),
                            "bs" + String.format("%.2f", item.getProducto().getPrecio()),
                            "bs" + String.format("%.2f", item.getSubtotal())
                        });
                    }
                }
                comboMesas.setEnabled(false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        actualizarTotal();
    }
    
    private void actualizarTotal() {
        double total = 0;
        try {
            Orden ordenActual = sistema.getOrdenActual();
            if (ordenActual != null) {
                total = ordenActual.getTotal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        lblTotal.setText(String.format("Total: bs%.2f", total));
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
}