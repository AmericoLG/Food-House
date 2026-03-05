public class Usuario {
    private int id;
    private String username;
    private String usuario;
    private String password;
    private String nombre;
    private String rol;
    private boolean activo;
    
    // Constructor para SistemaRestaurante (3 parámetros)
    public Usuario(int id, String usuario, String rol) {
        this.id = id;
        this.usuario = usuario;
        this.username = usuario;
        this.rol = rol;
        this.activo = true;
    }
    
    // Constructor para UsuarioDAO/Database (4 parámetros)
    public Usuario(String username, String password, String nombre, String rol) {
        this.username = username;
        this.usuario = username;
        this.password = password;
        this.nombre = nombre;
        this.rol = rol;
        this.activo = true;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getUsername() { return username != null ? username : usuario; }
    public void setUsername(String username) { 
        this.username = username;
        this.usuario = username;
    }
    
    public String getUsuario() { return usuario != null ? usuario : username; }
    public void setUsuario(String usuario) { 
        this.usuario = usuario;
        this.username = usuario;
    }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombre() { return nombre != null ? nombre : getUsuario(); }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    
    @Override
    public String toString() {
        return getNombre() + " (" + getRol() + ")";
    }
}