public class ItemOrden {
    private int id;
    private Producto producto;
    private int cantidad;
    private String notas;
    private double subtotal;
    private String estado;
    private int mesaId;
    private String mesero;
    
    public ItemOrden(int id, Producto producto, int cantidad, String notas) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.notas = notas;
        this.estado = "Pendiente";
        calcularSubtotal();
    }
    
    private void calcularSubtotal() {
        if (producto != null) {
            this.subtotal = producto.getPrecio() * cantidad;
        } else {
            this.subtotal = 0;
        }
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { 
        this.producto = producto;
        calcularSubtotal();
    }
    
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { 
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    
    public double getSubtotal() { return subtotal; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public int getMesaId() { return mesaId; }
    public void setMesaId(int mesaId) { this.mesaId = mesaId; }
    
    public String getMesero() { return mesero; }
    public void setMesero(String mesero) { this.mesero = mesero; }
}