package gestiondecanchas;

import java.time.LocalDate;

public class Reserva {
    private final int idReserva;       // Identificador único
    private final int canchaId;        // ID de la cancha asociada
    private final String rutSocio;     // Rut del socio que reservó
    private LocalDate fecha;           // Fecha de la reserva
    private BloqueHorario bloque;      // Bloque horario de la reserva

    public Reserva(int idReserva, int canchaId, String rutSocio, LocalDate fecha, BloqueHorario bloque) {
        this.idReserva = idReserva;
        this.canchaId = canchaId;
        this.rutSocio = rutSocio;
        this.fecha = fecha;
        this.bloque = bloque;
    }

    // ======= Getters =======
    public int getIdReserva() { return idReserva; }
    public int getCanchaId()  { return canchaId; }
    public String getRutSocio() { return rutSocio; }
    public LocalDate getFecha() { return fecha; }
    public BloqueHorario getBloque() { return bloque; }

    // ======= Setters =======
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setBloque(BloqueHorario bloque) { this.bloque = bloque; }

    // ======= Compatibilidad con código viejo (consola) =======
    /** Alias para compatibilidad con GestionDeCanchas: usa getCanchaId(). */
    public int getIdCancha() { return getCanchaId(); }

    // ======= Utilidad =======
    @Override
    public String toString() {
        return "Reserva #" + idReserva +
                " | Cancha: " + canchaId +
                " | Socio: " + rutSocio +
                " | Fecha: " + fecha +
                " | Bloque: " + bloque;
    }
}
