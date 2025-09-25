package gestiondecanchas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cancha {
    private final int id;
    private final String nombre;
    private final List<Reserva> reservas = new ArrayList<>();

    public Cancha(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // ======= Getters =======
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    /** Lista viva de reservas de esta cancha. */
    public List<Reserva> getReservas() { return reservas; }

    // ======= Operaciones =======
    /** Agrega una reserva a esta cancha. (No valida solapamientos) */
    public void agregarReserva(Reserva r) {
        if (r != null && !reservas.contains(r)) {
            reservas.add(r);
        }
    }

    /** Elimina una reserva por instancia. */
    public boolean eliminarReserva(Reserva r) {
        return reservas.remove(r);
    }

    /** Elimina una reserva por ID. */
    public boolean eliminarReservaPorId(int idReserva) {
        return reservas.removeIf(res -> res.getIdReserva() == idReserva);
    }

    /** Busca una reserva por ID (o null si no existe). */
    public Reserva buscarReserva(int idReserva) {
        for (Reserva r : reservas) {
            if (r.getIdReserva() == idReserva) return r;
        }
        return null;
    }

    /**
     * Verifica disponibilidad para fecha y bloque.
     * Retorna false si ya existe una reserva con misma fecha y bloque.
     */
    public boolean estaDisponible(LocalDate fecha, BloqueHorario bloque) {
        for (Reserva r : reservas) {
            if (r.getFecha().equals(fecha) && r.getBloque() == bloque) {
                return false;
            }
        }
        return true;
    }

    // ======= Compatibilidad con GestionDeCanchas =======
    public void cancelarReserva(Reserva r) {
        eliminarReserva(r);
    }

    // ======= Utilidad =======
    @Override
    public String toString() {
        return nombre + " (ID " + id + ")";
    }
}
