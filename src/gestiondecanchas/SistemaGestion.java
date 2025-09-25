package gestiondecanchas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;

public class SistemaGestion {
    private final List<Cancha> listaCanchas;
    private final Map<String, Socio> mapaSocios;
    private int proximoIdReserva = 1;

    public SistemaGestion() {
        this.listaCanchas = new ArrayList<>();
        this.mapaSocios = new HashMap<>();
        
        this.listaCanchas.add(new Cancha(1, "Cancha 1 - Principal"));
        this.listaCanchas.add(new Cancha(2, "Cancha 2 - Secundaria"));
        
        GestionArchivos ga = new GestionArchivos();
        ga.cargarSocios(this);
        ga.cargarReservas(this);
        
        sincronizarProximoId();
    }
    
    // Este método asegura que no se repitan los IDs de reserva
    private void sincronizarProximoId() {
        int maxId = 0;
        for (Cancha c : listaCanchas) {
            for (Reserva r : c.getReservas()) {
                if (r.getIdReserva() > maxId) {
                    maxId = r.getIdReserva();
                }
            }
        }
        this.proximoIdReserva = maxId + 1;
    }

    public int getProximoIdReserva() {
        return proximoIdReserva++;
    }
    
    public void eliminarSocio(String rut) { mapaSocios.remove(rut); }
    public int generarProximoIdReserva() {
        int max = 0;
        for (Cancha c : listaCanchas) {
            for (Reserva r : c.getReservas()) {
                if (r.getIdReserva() > max) max = r.getIdReserva();
            }
        }
        return max + 1;
    }
    public java.util.List<Reserva> getTodasLasReservas() {
        java.util.List<Reserva> out = new java.util.ArrayList<>();
        for (Cancha c : listaCanchas) out.addAll(c.getReservas());
        return out;
    }
    
    // ... (El resto de la clase se mantiene igual)
    public Collection<Socio> getSocios() { return mapaSocios.values(); }
    public void agregarOActualizarSocio(Socio socio) { this.mapaSocios.put(socio.getRut(), socio); }
    public Socio getSocioByRut(String rut) { return this.mapaSocios.get(rut); }
    public List<Cancha> getListaCanchas() { return new ArrayList<>(this.listaCanchas); }
    public Cancha getCancha(int id) {
        for (Cancha cancha : listaCanchas) {
            if (cancha.getId() == id) { return cancha; }
        }
        return null;
    }
}
