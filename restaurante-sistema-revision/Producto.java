public class Producto {
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private boolean disponible;
    private int tiempoPreparacion;
    private String categoria;
    
    // Constructor básico (5 parámetros)
    public Producto(int id, String nombre, String descripcion, double precio, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.tiempoPreparacion = 15;
        this.categoria = "General";
    }
    
    // Constructor completo (6 parámetros)
    public Producto(int id, String nombre, String descripcion, double precio, String categoria, int tiempoPreparacion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.tiempoPreparacion = tiempoPreparacion;
        this.disponible = true;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
    
    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
    
    public int getTiempoPreparacion() { return tiempoPreparacion; }
    public void setTiempoPreparacion(int tiempoPreparacion) { this.tiempoPreparacion = tiempoPreparacion; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    @Override
    public String toString() {
        return nombre + " - bs" + String.format("%.2f", precio);
    }
}