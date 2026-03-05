import java.io.*;
import java.util.ArrayList;

public class Database {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Producto> productos;
    private ArrayList<Mesa> mesas;
    private ArrayList<Orden> ordenes;
    private UsuarioDAO usuarioDAO;
    
    public Database() {
        // Inicializamos las listas
        usuarios = new ArrayList<>();
        productos = new ArrayList<>();
        mesas = new ArrayList<>();
        ordenes = new ArrayList<>();
        
        // Inicializamos el DAO para conectar con SQLite
        usuarioDAO = new UsuarioDAO();
        
        // CARGA DE DATOS REALES:
        // En lugar de crear datos ficticios, traemos lo que hay en SQLite
        cargarDatosDesdeBD();
        
        // Si después de cargar la lista sigue vacía, creamos los de prueba en la BD
        if (usuarios.isEmpty()) {
            crearDatosDefault();
        }
    }
    
    private void cargarDatosDesdeBD() {
        // Obtenemos los usuarios de la base de datos física
        this.usuarios = usuarioDAO.obtenerTodos();
        
        // Nota: Aquí se cargarían productos y mesas cuando tengas sus DAOs listos
        // Por ahora mantenemos los de prueba para productos y mesas para que no inicie vacío
    }
    
    private void crearDatosDefault() {
        // Usuarios: Los guardamos en la BD para que persistan
        Usuario admin = new Usuario("admin", "admin123", "Admin Principal", "Administrador");
        Usuario mesero = new Usuario("mesero", "mesero123", "Juan Perez", "Mesero");
        Usuario cocina = new Usuario("cocina", "cocina123", "Maria Garcia", "Cocinero");
        
        usuarioDAO.insertar(admin);
        usuarioDAO.insertar(mesero);
        usuarioDAO.insertar(cocina);
        
        // Recargamos la lista local desde la BD para asegurar sincronía
        this.usuarios = usuarioDAO.obtenerTodos();
        
        // Productos (Temporales en memoria hasta que tengas ProductoDAO)
        productos.add(new Producto(1, "Tacos al Pastor", "5 tacos con piña", 75.0, "Plato fuerte", 10));
        productos.add(new Producto(2, "Hamburguesa", "Carne de res con queso", 150.0, "Plato fuerte", 15));
        productos.add(new Producto(3, "Refresco", "Coca cola 600ml", 35.0, "Bebida", 1));
        
        // Mesas (Temporales en memoria hasta que tengas MesaDAO)
        for (int i = 1; i <= 8; i++) {
            mesas.add(new Mesa(i, 4));
        }
    }
    
    public ArrayList<Usuario> getUsuarios() { 
        return usuarios; 
    }
    
    public ArrayList<Producto> getProductos() { 
        return productos; 
    }
    
    public ArrayList<Mesa> getMesas() { 
        return mesas; 
    }
    
    public ArrayList<Orden> getOrdenes() { 
        return ordenes; 
    }
    
    public Usuario buscarUsuario(String identificador) {
        // Buscamos en la lista cargada desde la BD
        for (Usuario u : usuarios) {
            // Usamos getUsuario() que es el estándar de tu modelo actual
            if (u.getUsuario().equals(identificador)) {
                return u;
            }
        }
        return null;
    }
    
    public Producto buscarProducto(int id) {
        for (Producto p : productos) {
            if (p.getId() == id) return p;
        }
        return null;
    }
    
    public Mesa buscarMesa(int numero) {
        for (Mesa m : mesas) {
            if (m.getNumero() == numero) return m;
        }
        return null;
    }
    
    public void agregarOrden(Orden o) { 
        ordenes.add(o); 
    }
}