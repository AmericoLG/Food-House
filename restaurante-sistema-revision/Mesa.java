public class Mesa {
    private int numero;
    private int capacidad;
    private String estado;
    private String meseroAsignado;
    private boolean reservada;
    private String reservadaPara;
    
    public Mesa(int numero, int capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = "Libre";
        this.meseroAsignado = "";
        this.reservada = false;
        this.reservadaPara = "";
    }
    
    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }
    
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    
    public String getMeseroAsignado() { return meseroAsignado; }
    public void setMeseroAsignado(String meseroAsignado) { this.meseroAsignado = meseroAsignado; }
    
    public boolean isReservada() { return reservada; }
    public void setReservada(boolean reservada) { this.reservada = reservada; }
    
    public String getReservadaPara() { return reservadaPara; }
    public void setReservadaPara(String reservadaPara) { this.reservadaPara = reservadaPara; }
    
    @Override
    public String toString() {
        String info = "Mesa " + numero + " (Cap: " + capacidad + ")";
        if (reservada) info += " [RESERVADA]";
        return info;
    }
}