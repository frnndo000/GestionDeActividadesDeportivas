package gestiondecanchas;

import java.util.ArrayList;
import java.util.List;

public class Socio {
    private final String rut;           // Identificador único
    private String nombre;
    private String telefono;
    private final List<Reserva> reservas; // Reservas del socio

    public Socio(String rut, String nombre, String telefono) {
        this.rut = rut;
        this.nombre = nombre;
        this.telefono = telefono;
        this.reservas = new ArrayList<>();
    }

    // ======= Getters =======

    public String getRut() {
        return rut;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public List<Reserva> getReservas() {
        return reservas;
    }

    // ======= Setters =======

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    // ======= Métodos de negocio =======

    /** Asocia una nueva reserva a este socio. */
    public void agregarReserva(Reserva r) {
        if (r != null && !reservas.contains(r)) {
            reservas.add(r);
        }
    }

    /** Elimina una reserva asociada a este socio. */
    public void eliminarReserva(Reserva r) {
        reservas.remove(r);
    }

    // ======= Utilidad =======

    @Override
    public String toString() {
        return "Socio{" +
                "rut='" + rut + '\'' +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", reservas=" + reservas.size() +
                '}';
    }

    Object getMisReservas() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
