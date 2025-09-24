package gestiondecanchas;
//Mensaje de prueba
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collection;
import java.util.Map;
        
public class SistemaGestion {
    private final List<Cancha> listaCanchas ;
    private final Map<String, Socio> mapaSocios;
    
    public SistemaGestion() {
        this.listaCanchas = new ArrayList<>() ;
        this.mapaSocios = new HashMap<>();
        
        this.listaCanchas.add(new Cancha(1, "Cancha 1 - Principal"));
        this.listaCanchas.add(new Cancha(2, "Cancha 2 - Secundaria"));
        
        GestionArchivos ga = new GestionArchivos();
        ga.cargarSocios(this);
        ga.cargarReservas(this);
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