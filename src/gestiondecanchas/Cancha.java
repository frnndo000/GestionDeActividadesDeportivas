package gestiondecanchas;
//Mensaje de prueba
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cancha {
    private int id ;
    private String nombre ;
    private List<Reserva> reservas ;
    
    public Cancha(int id, String nombre) {
        this.id = id ;
        this.nombre = nombre ;
        this.reservas = new ArrayList<>() ;
    }
    
    public int getId() { return id ; }
    public void setId(int nuevoId) { this.id = nuevoId ; }
    public String getNombre() { return nombre ; }
    public void setNombre(String nuevoNombre) { this.nombre = nuevoNombre ; }
    public List<Reserva> getReservas() {
        return new ArrayList<>(this.reservas); 
    }
    
    public Reserva buscarReserva(int id) {
        for (Reserva r: reservas) {
            if (r.getIdReserva() == id) return r;
        }
        return null;
    }
    
    public Reserva buscarReserva(LocalDate fecha, BloqueHorario bloque) {
        for (Reserva r : reservas) {
            if (r.getFecha().equals(fecha) && r.getBloque() == bloque) return r ;
        }
        return null;
    }
    
    public boolean estaDisponible(LocalDate fecha, BloqueHorario bloque){
        return buscarReserva(fecha,bloque) == null;
    }
    
    public void agregarReservaDesdeArchivo(Reserva reserva) {
        this.reservas.add(reserva);
    }
    
    public void agregarReserva(Reserva res) {
        if (!estaDisponible(res.getFecha(), res.getBloque())) {
            throw new IllegalStateException("Bloque no disponible para " + res.getFecha() + " " + res.getBloque());
        }
        reservas.add(res);
    }
    
    public void cancelarReserva(Reserva reserva) {
        this.reservas.remove(reserva);
    }
}