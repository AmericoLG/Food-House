import java.util.ArrayList;
import java.util.Date;

public class Orden {
    private int id;
    private Mesa mesa;
    private String mesero;
    private ArrayList<ItemOrden> items;
    private String estado;
    private Date fecha;
    private double total;
    private int nextItemId;
    
    public Orden(int id, Mesa mesa, String mesero) {
        this.id = id;
        this.mesa = mesa;
        this.mesero = mesero;
        this.items = new ArrayList<>();
        this.estado = "Pendiente";
        this.fecha = new Date();
        this.total = 0.0;
        this.nextItemId = 1;
    }
    
    public void agregarItem(Producto producto, int cantidad, String notas) {
        int itemId = nextItemId++;
        items.add(new ItemOrden(itemId, producto, cantidad, notas));
        calcularTotal();
    }
    
    public void agregarItem(ItemOrden item) {
        items.add(item);
        calcularTotal();
    }
    
    public void calcularTotal() {
        total = 0;
        for (ItemOrden item : items) {
            total += item.getSubtotal();
        }
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    
    public String getMesero() { return mesero; }
    public void setMesero(String mesero) { this.mesero = mesero; }
    
    public ArrayList<ItemOrden> getItems() { return items; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    
    @Override
    public String toString() {
        return "Orden " + id + " - Mesa " + mesa.getNumero() + " - " + mesero;
    }
}