package gestiondecanchas;
//mensaje de prueba
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
    
    public Socio getSocioByRut(String rut) throws SocioNoEncontradoException {
        Socio socio = this.mapaSocios.get(rut);
        if (socio == null) {
            throw new SocioNoEncontradoException("El socio con RUT " + rut + " no fue encontrado.");
        }
        return socio;
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
    
    public Reserva crearReserva(String rutSocio, int idCancha, LocalDate fecha, BloqueHorario bloque)
            throws SocioNoEncontradoException, ReservaConflictException {

        // 1. Validar que el socio exista (esto ya lanza la excepción)
        Socio socio = getSocioByRut(rutSocio);

        // 2. Validar que la cancha exista
        Cancha cancha = getCancha(idCancha);
        if (cancha == null) {
            // Este es un caso de error que no debería ocurrir si la GUI funciona bien
            throw new IllegalArgumentException("La cancha con ID " + idCancha + " no existe.");
        }

        // 3. Lanzar nuestra excepción si la cancha no está disponible
        if (!cancha.estaDisponible(fecha, bloque)) {
            throw new ReservaConflictException("La cancha '" + cancha.getNombre() + "' ya está ocupada en ese horario.");
        }

        // 4. Si todo es válido, crear la reserva
        int nuevoId = getProximoIdReserva();
        Reserva nuevaReserva = new Reserva(nuevoId, cancha.getId(), socio.getRut(), fecha, bloque);

        cancha.agregarReserva(nuevaReserva);
        socio.agregarReserva(nuevaReserva);

        // 5. Persistir en el archivo
        new GestionArchivos().agregarReservaACSV(nuevaReserva);

        return nuevaReserva;
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
        try {
            Socio socio = getSocioByRut(reserva.getRutSocio());
            socio.eliminarReserva(reserva);
        } catch (SocioNoEncontradoException e) {
            // Si el socio no se encuentra, simplemente lo ignoramos y continuamos.
            // Opcional: puedes imprimir un mensaje en la consola de error.
            System.err.println("Advertencia al eliminar reserva: " + e.getMessage());
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