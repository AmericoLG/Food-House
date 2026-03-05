import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VentanaLogin extends JFrame {
    private SistemaRestaurante sistema;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> comboRol;
    
    // Colores del tema restaurante
    private final Color COLOR_FONDO = new Color(61, 41, 20);      // Café oscuro
    private final Color COLOR_FONDO_CLARO = new Color(92, 61, 30); // Café medio
    private final Color COLOR_DORADO = new Color(212, 175, 55);    // Dorado
    private final Color COLOR_TEXTO = new Color(245, 245, 220);    // Beige
    private final Color COLOR_BOTON = new Color(139, 69, 19);      // Café silla
    
    public VentanaLogin() {
        sistema = new SistemaRestaurante();
        inicializar();
    }
    
    private void inicializar() {
        setTitle("Food House - Sistema de Gestión");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal con imagen de fondo tipo restaurante
        JPanel panelPrincipal = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Gradiente café elegante
                int w = getWidth(), h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, COLOR_FONDO, w, h, new Color(40, 25, 10));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
                
                // Patrón sutil de líneas decorativas
                g2d.setColor(new Color(212, 175, 55, 30));
                for (int i = 0; i < w; i += 40) {
                    g2d.drawLine(i, 0, i, h);
                }
            }
        };
        panelPrincipal.setLayout(null);
        
        // Panel del formulario con efecto vidrio
        JPanel panelFormulario = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fondo semitransparente café
                g2d.setColor(new Color(92, 61, 30, 240));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Borde dorado
                g2d.setColor(COLOR_DORADO);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
            }
        };
        panelFormulario.setLayout(null);
        panelFormulario.setBounds(25, 25, 450, 570);
        panelFormulario.setOpaque(false);
        
        // Logo/Icono con estilo elegante
        JLabel lblIcono = new JLabel("🍽️", SwingConstants.CENTER);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 80));
        lblIcono.setForeground(COLOR_DORADO);
        lblIcono.setBounds(0, 20, 450, 90);
        panelFormulario.add(lblIcono);
        
        // Título principal
        JLabel lblTitulo = new JLabel("FOOD HOUSE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Georgia", Font.BOLD, 32));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(0, 110, 450, 40);
        panelFormulario.add(lblTitulo);
        
        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Gestión Gastronómica", SwingConstants.CENTER);
        lblSubtitulo.setFont(new Font("Georgia", Font.ITALIC, 16));
        lblSubtitulo.setForeground(COLOR_TEXTO);
        lblSubtitulo.setBounds(0, 150, 450, 25);
        panelFormulario.add(lblSubtitulo);
        
        // Línea decorativa
        JPanel lineaDecorativa = new JPanel();
        lineaDecorativa.setBackground(COLOR_DORADO);
        lineaDecorativa.setBounds(75, 185, 300, 2);
        panelFormulario.add(lineaDecorativa);
        
        // Campo Usuario
        JLabel lblUsuario = new JLabel("👤 Usuario");
        lblUsuario.setBounds(50, 210, 350, 25);
        lblUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUsuario.setForeground(COLOR_TEXTO);
        panelFormulario.add(lblUsuario);
        
        txtUsuario = new JTextField();
        txtUsuario.setBounds(50, 235, 350, 45);
        txtUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUsuario.setBackground(new Color(245, 245, 220, 240));
        txtUsuario.setForeground(Color.BLACK);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFormulario.add(txtUsuario);
        
        // Campo Contraseña
        JLabel lblPassword = new JLabel("🔒 Contraseña");
        lblPassword.setBounds(50, 295, 350, 25);
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPassword.setForeground(COLOR_TEXTO);
        panelFormulario.add(lblPassword);
        
        txtPassword = new JPasswordField();
        txtPassword.setBounds(50, 320, 350, 45);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPassword.setBackground(new Color(245, 245, 220, 240));
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFormulario.add(txtPassword);
        
        // Campo Rol
        JLabel lblRol = new JLabel("👔 Rol");
        lblRol.setBounds(50, 380, 350, 25);
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblRol.setForeground(COLOR_TEXTO);
        panelFormulario.add(lblRol);
        
        String[] roles = {"Seleccionar rol", "Administrador", "Mesero", "Cocinero"};
        comboRol = new JComboBox<String>(roles);
        comboRol.setBounds(50, 405, 350, 45);
        comboRol.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboRol.setBackground(new Color(245, 245, 220, 240));
        comboRol.setForeground(Color.BLACK);
        comboRol.setBorder(BorderFactory.createLineBorder(COLOR_DORADO, 2));
        panelFormulario.add(comboRol);
        
        // Botón Login con estilo elegante
        JButton btnLogin = new JButton("INICIAR SESIÓN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(COLOR_DORADO.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(COLOR_DORADO);
                } else {
                    g2.setColor(COLOR_BOTON);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                
                super.paintComponent(g);
            }
        };
        btnLogin.setBounds(50, 475, 350, 55);
        btnLogin.setFont(new Font("Georgia", Font.BOLD, 18));
        btnLogin.setForeground(COLOR_TEXTO);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> intentarLogin());
        panelFormulario.add(btnLogin);
        
        // Info de prueba
        JLabel lblInfo = new JLabel("Usuarios: admin/admin123 | mesero/mesero123 | cocina/cocina123", SwingConstants.CENTER);
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(210, 180, 140));
        lblInfo.setBounds(0, 545, 450, 20);
        panelFormulario.add(lblInfo);
        
        panelPrincipal.add(panelFormulario);
        add(panelPrincipal);
    }
    
    private void intentarLogin() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        String rol = (String) comboRol.getSelectedItem();
        
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese usuario y contraseña", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if ("Seleccionar rol".equals(rol)) {
            JOptionPane.showMessageDialog(this, "Seleccione un rol", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (sistema.login(usuario, password)) {
            String rolUsuario = sistema.getRolUsuario();
            
            if (!rolUsuario.equals(rol)) {
                JOptionPane.showMessageDialog(this, "El rol no coincide con el usuario", "Error", JOptionPane.ERROR_MESSAGE);
                sistema.logout();
                return;
            }
            
            JOptionPane.showMessageDialog(this, 
                "¡Bienvenido " + sistema.getUsuarioActual().getNombre() + "!", 
                "Login exitoso", JOptionPane.INFORMATION_MESSAGE);
            
            abrirVentanaPrincipal();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void abrirVentanaPrincipal() {
        String rol = sistema.getRolUsuario();
        JFrame ventana = null;
        
        switch(rol) {
            case "Administrador":
                ventana = new VentanaAdmin(sistema);
                break;
            case "Mesero":
                ventana = new VentanaMesero(sistema);
                break;
            case "Cocinero":
                ventana = new VentanaCocina(sistema);
                break;
        }
        
        if (ventana != null) {
            ventana.setVisible(true);
            this.dispose();
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}