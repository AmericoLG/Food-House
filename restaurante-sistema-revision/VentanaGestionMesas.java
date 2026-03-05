import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.sql.SQLException;


public class VentanaGestionMesas extends JFrame {
    private SistemaRestaurante sistema;
    private JTable tablaMesas;
    private DefaultTableModel modeloTabla;
    
    public VentanaGestionMesas(SistemaRestaurante sistema) {
        this.sistema = sistema;
        inicializar();
    }
    
    private void inicializar() {
        if (sistema == null) {
            JOptionPane.showMessageDialog(null, "Error: Sistema no inicializado");
            dispose();
            return;
        }
        
        setTitle("Gestión de Mesas - Administrador");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel lblTitulo = new JLabel("GESTIÓN DE MESAS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 28));
        lblTitulo.setForeground(new Color(102, 126, 234));
        panel.add(lblTitulo, BorderLayout.NORTH);
        
        // Tabla de mesas
        String[] columnas = {"Número", "Capacidad", "Estado", "Mesero", "Reservada", "Reservada Para", "Acciones"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaMesas = new JTable(modeloTabla);
        tablaMesas.setRowHeight(35);
        tablaMesas.setFont(new Font("Arial", Font.PLAIN, 13));
        tablaMesas.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tablaMesas.getTableHeader().setBackground(new Color(102, 126, 234));
        tablaMesas.getTableHeader().setForeground(Color.WHITE);
        
        // Colores según estado
        tablaMesas.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String estado = (String) table.getValueAt(row, 2);
                boolean reservada = "Sí".equals(table.getValueAt(row, 4));
                
                if (!isSelected) {
                    if (reservada) {
                        c.setBackground(new Color(255, 243, 205));
                        c.setForeground(new Color(133, 100, 4));
                    } else if ("Ocupada".equals(estado)) {
                        c.setBackground(new Color(248, 215, 218));
                        c.setForeground(new Color(114, 28, 36));
                    } else {
                        c.setBackground(new Color(212, 237, 218));
                        c.setForeground(new Color(21, 87, 36));
                    }
                }
                return c;
            }
        });
        
        JScrollPane scroll = new JScrollPane(tablaMesas);
        panel.add(scroll, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton btnReservar = new JButton("Reservar");
        btnReservar.setBackground(new Color(255, 193, 7));
        btnReservar.setForeground(Color.BLACK);
        
        JButton btnCancelarReserva = new JButton("Cancelar Reserva");
        btnCancelarReserva.setBackground(new Color(108, 117, 125));
        btnCancelarReserva.setForeground(Color.BLACK);
        
        JButton btnMarcarPagada = new JButton("Marcar Pagada/Liberar");
        btnMarcarPagada.setBackground(new Color(40, 167, 69));
        btnMarcarPagada.setForeground(Color.BLACK);
        
        JButton btnEditar = new JButton("Editar Capacidad");
        btnEditar.setBackground(new Color(0, 123, 255));
        btnEditar.setForeground(Color.BLACK);
        
        JButton btnAgregar = new JButton("Agregar Mesa");
        btnAgregar.setBackground(new Color(23, 162, 184));
        btnAgregar.setForeground(Color.BLACK);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(220, 53, 69));
        btnEliminar.setForeground(Color.BLACK);
        
        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.setBackground(new Color(108, 117, 125));
        btnActualizar.setForeground(Color.BLACK);
        
        panelBotones.add(btnReservar);
        panelBotones.add(btnCancelarReserva);
        panelBotones.add(btnMarcarPagada);
        panelBotones.add(btnEditar);
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        
        panel.add(panelBotones, BorderLayout.SOUTH);
        
        add(panel);
        
        // Acciones
        btnReservar.addActionListener(e -> reservarMesa());
        btnCancelarReserva.addActionListener(e -> cancelarReserva());
        btnMarcarPagada.addActionListener(e -> marcarPagada());
        btnEditar.addActionListener(e -> editarCapacidad());
        btnAgregar.addActionListener(e -> agregarMesa());
        btnEliminar.addActionListener(e -> eliminarMesa());
        btnActualizar.addActionListener(e -> cargarMesas());
        
        cargarMesas();
    }
    
    private void cargarMesas() {
        modeloTabla.setRowCount(0);
        try {
            ArrayList<Mesa> mesas = sistema.getMesas();
            if (mesas != null) {
                for (int i = 0; i < mesas.size(); i++) {
                    Mesa m = mesas.get(i);
                    if (m != null) {
                        Object[] fila = new Object[7];
                        fila[0] = m.getNumero();
                        fila[1] = m.getCapacidad();
                        fila[2] = m.getEstado();
                        fila[3] = (m.getMeseroAsignado() == null || m.getMeseroAsignado().isEmpty()) ? "-" : m.getMeseroAsignado();
                        fila[4] = m.isReservada() ? "Sí" : "No";
                        fila[5] = (m.getReservadaPara() == null || m.getReservadaPara().isEmpty()) ? "-" : m.getReservadaPara();
                        fila[6] = "Acciones";
                        modeloTabla.addRow(fila);
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al cargar mesas: " + e.getMessage());
        }
    }
    
    private void reservarMesa() {
        int fila = tablaMesas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }
        
        int numeroMesa = (Integer) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 2);
        boolean reservada = "Sí".equals(modeloTabla.getValueAt(fila, 4));
        
        if ("Ocupada".equals(estado)) {
            JOptionPane.showMessageDialog(this, "No se puede reservar una mesa ocupada");
            return;
        }
        
        if (reservada) {
            JOptionPane.showMessageDialog(this, "La mesa ya está reservada");
            return;
        }
        
        String nombreCliente = JOptionPane.showInputDialog(this, "Nombre del cliente para la reserva:");
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) return;
        
        try {
            sistema.reservarMesa(numeroMesa, nombreCliente.trim());
            cargarMesas();
            JOptionPane.showMessageDialog(this, "Mesa reservada correctamente");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void cancelarReserva() {
        int fila = tablaMesas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }
        
        int numeroMesa = (Integer) modeloTabla.getValueAt(fila, 0);
        boolean reservada = "Sí".equals(modeloTabla.getValueAt(fila, 4));
        
        if (!reservada) {
            JOptionPane.showMessageDialog(this, "La mesa no está reservada");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Cancelar reserva de la mesa " + numeroMesa + "?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                sistema.cancelarReservaMesa(numeroMesa);
                cargarMesas();
                JOptionPane.showMessageDialog(this, "Reserva cancelada");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void marcarPagada() {
        int fila = tablaMesas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }
        
        int numeroMesa = (Integer) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 2);
        
        if (!"Ocupada".equals(estado)) {
            JOptionPane.showMessageDialog(this, "La mesa no está ocupada");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Marcar mesa " + numeroMesa + " como PAGADA y liberar?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                sistema.marcarMesaPagada(numeroMesa);
                cargarMesas();
                JOptionPane.showMessageDialog(this, "Mesa liberada correctamente");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void editarCapacidad() {
        int fila = tablaMesas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }
        
        int numeroMesa = (Integer) modeloTabla.getValueAt(fila, 0);
        int capacidadActual = (Integer) modeloTabla.getValueAt(fila, 1);
        
        String input = JOptionPane.showInputDialog(this, 
            "Nueva capacidad para Mesa " + numeroMesa + ":", 
            capacidadActual);
        
        if (input != null && !input.isEmpty()) {
            try {
                int nuevaCapacidad = Integer.parseInt(input);
                if (nuevaCapacidad > 0 && nuevaCapacidad <= 20) {
                    sistema.editarCapacidadMesa(numeroMesa, nuevaCapacidad);
                    cargarMesas();
                    JOptionPane.showMessageDialog(this, "Capacidad actualizada");
                } else {
                    JOptionPane.showMessageDialog(this, "Capacidad entre 1 y 20");
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
    
    private void agregarMesa() {
        String numInput = JOptionPane.showInputDialog(this, "Número de nueva mesa:");
        if (numInput == null || numInput.isEmpty()) return;
        
        String capInput = JOptionPane.showInputDialog(this, "Capacidad:", "4");
        if (capInput == null || capInput.isEmpty()) return;
        
        try {
            int numero = Integer.parseInt(numInput);
            int capacidad = Integer.parseInt(capInput);
            
            ArrayList<Mesa> mesas = sistema.getMesas();
            for (int i = 0; i < mesas.size(); i++) {
                Mesa m = mesas.get(i);
                if (m != null && m.getNumero() == numero) {
                    JOptionPane.showMessageDialog(this, "Ya existe mesa con ese número");
                    return;
                }
            }
            
            sistema.agregarMesa(numero, capacidad);
            cargarMesas();
            JOptionPane.showMessageDialog(this, "Mesa agregada");
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Números inválidos");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
    
    private void eliminarMesa() {
        int fila = tablaMesas.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una mesa");
            return;
        }
        
        int numeroMesa = (Integer) modeloTabla.getValueAt(fila, 0);
        String estado = (String) modeloTabla.getValueAt(fila, 2);
        
        if (!"Libre".equals(estado)) {
            JOptionPane.showMessageDialog(this, "No se puede eliminar: mesa ocupada o reservada");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "¿Eliminar Mesa " + numeroMesa + "?", 
            "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                sistema.eliminarMesa(numeroMesa);
                cargarMesas();
                JOptionPane.showMessageDialog(this, "Mesa eliminada");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }
}