package gestiondecanchas;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class SistemaGestion {
    private final List<Cancha> listaCanchas;
    private final Map<String, Socio> mapaSocios;
    private int proximoIdReserva = 1;

    public SistemaGestion() {
        this.listaCanchas = new ArrayList<>();
        this.mapaSocios = new HashMap<>();

        GestionArchivos ga = new GestionArchivos();
        ga.cargarCanchas(this);
        ga.cargarSocios(this);
        ga.cargarReservas(this);
        sincronizarProximoId();
    }

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
    
    public void agregarCancha(Cancha c) {
        if (c != null && getCancha(c.getId()) == null) {
            this.listaCanchas.add(c);
        }
    }

    public boolean eliminarCancha(int id) {
        return this.listaCanchas.removeIf(c -> c.getId() == id);
    }
    
    public int getProximoIdCancha() {
        return listaCanchas.stream()
                .mapToInt(Cancha::getId)
                .max()
                .orElse(0) + 1;
    }

    private Reserva buscarReserva(int canchaId, int idReserva) {
        Cancha c = getCancha(canchaId);
        if (c == null) return null;
        for (Reserva r : c.getReservas()) {
            if (r.getIdReserva() == idReserva) return r;
        }
        return null;
    }

    public int getProximoIdReserva() {
        return proximoIdReserva++;
    }
    
    public Collection<Socio> getSocios() { return mapaSocios.values(); }
    
    public Collection<Socio> filtrarSociosFrecuentes(int minimoReservas) {
        return this.mapaSocios.values()
            .stream()
            .filter(socio -> socio.getReservas().size() >= minimoReservas)
            .collect(Collectors.toList());
    }

    public void agregarOActualizarSocio(Socio socio) {
        this.mapaSocios.put(socio.getRut(), socio);
    }

    public Socio getSocioByRut(String rut) {
        return this.mapaSocios.get(rut);
    }

    public void eliminarSocio(String rut) {
        mapaSocios.remove(rut);
    }

    public List<Cancha> getListaCanchas() {
        return new ArrayList<>(this.listaCanchas);
    }

    public Cancha getCancha(int id) {
        for (Cancha cancha : listaCanchas) {
            if (cancha.getId() == id) return cancha;
        }
        return null;
    }
    
    public Cancha getCancha(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return null;
        }
        for (Cancha cancha : listaCanchas) {
            if (cancha.getNombre().equalsIgnoreCase(nombre.trim())) {
                return cancha;
            }
        }
        return null;
    }

    public List<Reserva> getTodasLasReservas() {
        List<Reserva> out = new ArrayList<>();
        for (Cancha c : listaCanchas) out.addAll(c.getReservas());
        return out;
    }

    public List<Reserva> listarReservasOrdenadas(int canchaId) {
        Cancha c = getCancha(canchaId);
        if (c == null) return Collections.emptyList();
        return c.getReservas().stream()
                .sorted(Comparator
                        .comparing(Reserva::getFecha)
                        .thenComparing(Reserva::getBloque))
                .collect(Collectors.toList());
    }

    /** Elimina una reserva por ID dentro de una cancha */
    public boolean eliminarReserva(int canchaId, int idReserva) {
        Cancha c = getCancha(canchaId);
        if (c == null) return false;
        
        // Buscar la reserva para obtener info del socio
        Reserva reserva = null;
        for (Reserva r : c.getReservas()) {
            if (r.getIdReserva() == idReserva) {
                reserva = r;
                break;
            }
        }
        
        if (reserva == null) return false;
        
        // Eliminar del socio si existe
        Socio socio = getSocioByRut(reserva.getRutSocio());
        if (socio != null) {
            socio.eliminarReserva(reserva);
        }
        
        // Eliminar de la cancha
        return c.eliminarReservaPorId(idReserva);
    }

    public boolean editarReservaFecha(int canchaId, int idReserva, LocalDate nuevaFecha) {
        Reserva r = buscarReserva(canchaId, idReserva);
        if (r == null) return false;
        r.setFecha(nuevaFecha);
        return true;
    }

    public boolean editarReservaBloque(int canchaId, int idReserva, BloqueHorario nuevoBloque) {
        Reserva r = buscarReserva(canchaId, idReserva);
        if (r == null) return false;
        r.setBloque(nuevoBloque);
        return true;
    }
}