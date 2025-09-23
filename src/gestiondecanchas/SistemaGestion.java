package gestiondecanchas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;
import java.time.LocalDate;
        
public class SistemaGestion {
    private List<Cancha> listaCanchas ;
    private Map<String, Socio> mapaSocios;
    
    public SistemaGestion() {
        this.listaCanchas = new ArrayList<>() ;
        this.mapaSocios = new HashMap<>();
        
        cargarDatosIniciales();
    }
    
    private void cargarDatosIniciales() {
        Cancha cancha1 = new Cancha(1, "Cancha 1 - Principal");
        Cancha cancha2 = new Cancha(2, "Cancha 2 - Secundaria");
        
        Socio socio1 = new Socio("1", "Juan Perez", "912345678");
        Socio socio2 = new Socio("2", "Ana Garcia", "987654321");
        
        this.mapaSocios.put(socio1.getRut(), socio1);
        this.mapaSocios.put(socio2.getRut(), socio2);
        
        Reserva reservaPrueba = new Reserva(99, "1-1", LocalDate.now(), BloqueHorario.B19_20);
        cancha1.agregarReserva(reservaPrueba);
        
        this.listaCanchas.add(cancha1);
        this.listaCanchas.add(cancha2);
    }
    
    public Collection<Socio> getSocios() {
        return mapaSocios.values();
    }
    
    public void agregarOActualizarSocio(Socio socio) {
        this.mapaSocios.put(socio.getRut(), socio);
    }
    
    public Socio getSocioByRut(String rut) {
        return this.mapaSocios.get(rut);
    }
    
    public List<Cancha> getListaCanchas() { 
        return new ArrayList<>(this.listaCanchas) ; 
    }
    
    public Cancha getCancha(int id) {
        for (Cancha cancha : listaCanchas) {
            if (cancha.getId() == id) {
                return cancha;
            }
        }
        return null;
    }
    
    public Cancha getCancha(String nombre) {
        for (Cancha cancha : this.listaCanchas) {
            if (cancha.getNombre().equalsIgnoreCase(nombre)) {
                return cancha ;
            }
        }
        return null;
    }
}