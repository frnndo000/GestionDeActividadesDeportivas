package gestiondecanchas;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un socio del club, con sus datos personales y la
 * lista de reservas que ha realizado.
 */
public class Socio {
    private final String rut;           // Identificador único del socio
    private String nombre;
    private String telefono;
    private final List<Reserva> reservas; // Colección de reservas asociadas al socio

    /**
     * Constructor para crear un nuevo socio.
     * @param rut El RUT del socio.
     * @param nombre El nombre completo del socio.
     * @param telefono El número de teléfono de contacto.
     */
    public Socio(String rut, String nombre, String telefono) {
        this.rut = rut;
        this.nombre = nombre;
        this.telefono = telefono;
        this.reservas = new ArrayList<>();
    }

    // ======= Getters =======
    public String getRut() { return rut; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public List<Reserva> getReservas() { return new ArrayList<>(reservas); } // Devuelve una copia para proteger la lista original

    // ======= Setters =======
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    // ======= Métodos de Negocio =======

    /**
     * Asocia una nueva reserva a este socio.
     * @param r La reserva a agregar.
     */
    public void agregarReserva(Reserva r) {
        if (r != null && !reservas.contains(r)) {
            reservas.add(r);
        }
    }

    // --- CUMPLIMIENTO DE SOBRECARGA (SIA1.6) ---

    /**
     * Elimina una reserva asociada a este socio, buscándola por su instancia.
     * (Sobrecarga 1 de 2)
     * @param r La instancia de la reserva a eliminar.
     */
    public void eliminarReserva(Reserva r) {
        if (r != null) {
            this.reservas.remove(r);
        }
    }

    /**
     * Elimina una reserva asociada a este socio, buscándola por su ID.
     * @param idReserva El ID numérico de la reserva a eliminar.
     * @return true si la reserva fue encontrada y eliminada, false en caso contrario.
     */
    public boolean eliminarReserva(int idReserva) {
        return this.reservas.removeIf(reserva -> reserva.getIdReserva() == idReserva);
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
}