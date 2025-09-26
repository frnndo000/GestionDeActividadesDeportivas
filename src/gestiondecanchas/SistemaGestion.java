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

        // Cargar canchas base
        this.listaCanchas.add(new Cancha(1, "Cancha 1 - Principal"));
        this.listaCanchas.add(new Cancha(2, "Cancha 2 - Secundaria"));

        // Cargar datos desde archivos
        GestionArchivos ga = new GestionArchivos();
        ga.cargarSocios(this);
        ga.cargarReservas(this);

        // Ajustar el contador de IDs de reserva
        sincronizarProximoId();
    }

    // ======= Métodos internos =======

    /** Ajusta el próximo ID de reserva al máximo existente + 1. */
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

    /** Busca una reserva específica en una cancha. */
    private Reserva buscarReserva(int canchaId, int idReserva) {
        Cancha c = getCancha(canchaId);
        if (c == null) return null;
        for (Reserva r : c.getReservas()) {
            if (r.getIdReserva() == idReserva) return r;
        }
        return null;
    }

    // ======= Gestión de IDs =======

    /** Devuelve y avanza el próximo ID de reserva. */
    public int getProximoIdReserva() {
        return proximoIdReserva++;
    }

    // ======= Gestión de socios =======

    public Collection<Socio> getSocios() { return mapaSocios.values(); }
    
    public Collection<Socio> filtrarSociosFrecuentes(int minimoReservas) {
        return this.mapaSocios.values()
            .stream()
            .filter(socio -> socio.getMisReservas().size() >= minimoReservas)
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

    // ======= Gestión de canchas =======

    public List<Cancha> getListaCanchas() {
        return new ArrayList<>(this.listaCanchas);
    }

    public Cancha getCancha(int id) {
        for (Cancha cancha : listaCanchas) {
            if (cancha.getId() == id) return cancha;
        }
        return null;
    }

    // ======= Gestión de reservas (2ª colección anidada) =======

    /** Devuelve todas las reservas de todas las canchas. */
    public List<Reserva> getTodasLasReservas() {
        List<Reserva> out = new ArrayList<>();
        for (Cancha c : listaCanchas) out.addAll(c.getReservas());
        return out;
    }

    /** Lista reservas de una cancha ordenadas por fecha y bloque. */
    public List<Reserva> listarReservasOrdenadas(int canchaId) {
        Cancha c = getCancha(canchaId);
        if (c == null) return Collections.emptyList();
        return c.getReservas().stream()
                .sorted(Comparator
                        .comparing(Reserva::getFecha)
                        .thenComparing(Reserva::getBloque))
                .collect(Collectors.toList());
    }

    /** Elimina una reserva por ID dentro de una cancha. */
    public boolean eliminarReserva(int canchaId, int idReserva) {
        Cancha c = getCancha(canchaId);
        if (c == null) return false;
        return c.getReservas().removeIf(r -> r.getIdReserva() == idReserva);
    }

    /** Edita la fecha de una reserva. */
    public boolean editarReservaFecha(int canchaId, int idReserva, LocalDate nuevaFecha) {
        Reserva r = buscarReserva(canchaId, idReserva);
        if (r == null) return false;
        r.setFecha(nuevaFecha);
        return true;
    }

    /** Edita el bloque de una reserva (usa BloqueHorario). */
    public boolean editarReservaBloque(int canchaId, int idReserva, BloqueHorario nuevoBloque) {
        Reserva r = buscarReserva(canchaId, idReserva);
        if (r == null) return false;
        r.setBloque(nuevoBloque);
        return true;
    }
}
