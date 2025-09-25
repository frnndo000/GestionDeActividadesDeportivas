package gestiondecanchas;

import java.util.ArrayList;
import java.util.List;

public class Socio{
    private String rut ;
    private String nombre ;
    private String telefono ;
    private List<Reserva> misReservas;
    
    public Socio(String rut, String nombre, String telefono) {
        this.rut = rut ;
        this.nombre = nombre ;
        this.telefono = telefono ;
        this.misReservas = new ArrayList<>();
    }
    
    public Socio(String rut, String nombre) {
        this.rut = rut ;
        this.nombre = nombre ;
        this.telefono = "No especifica" ;   
    }
    
    public void agregarReserva(Reserva reserva) {
        this.misReservas.add(reserva);
    }
    
    public List<Reserva> getMisReservas() {
        return new ArrayList<>(this.misReservas); 
    }
    
    public void cancelarReserva(Reserva reserva) {
        this.misReservas.remove(reserva);
    }
    
    @Override
    public String toString() { return rut + " - " + nombre; } // Socio
    
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
