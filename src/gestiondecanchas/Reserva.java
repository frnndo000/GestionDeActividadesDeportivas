package gestiondecanchas;
//Mensaje de prueba
import java.time.LocalDate;

public class Reserva {
    private int idReserva;
    private int idCancha;
    private String rutSocio;
    private LocalDate fecha;
    private BloqueHorario bloque;
    
    public Reserva(int idReserva, int idCancha, String rutSocio, LocalDate fecha, BloqueHorario bloque) {
        this.idReserva = idReserva;
        this.idCancha = idCancha;
        this.rutSocio = rutSocio;
        this.fecha = fecha;
        this.bloque = bloque;
    }
    
    public int getIdCancha() { return idCancha; }
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BloqueHorario getBloque() { return bloque; }
    public void setBloque(BloqueHorario bloque) { this.bloque = bloque; }
    public String getRutSocio() { return rutSocio; }
    public void setRutSocio(String rutSocio) { this.rutSocio = rutSocio; }
}

