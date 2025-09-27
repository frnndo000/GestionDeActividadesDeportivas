package gestiondecanchas;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Persistencia muy simple en CSV:
 *  - data/socios.csv     -> rut,nombre,telefono
 *  - data/reservas.csv   -> idReserva,canchaId,rutSocio,fechaISO,bloqueEnum
 *
 * Si los archivos no existen, se crean vacíos.
 */
public class GestionArchivos {

    private static final Path DIR_DATA = Paths.get("data");
    private static final Path FILE_SOCIOS = DIR_DATA.resolve("socios.csv");
    private static final Path FILE_RESERVAS = DIR_DATA.resolve("reservas.csv");
    private static final Path FILE_CANCHAS = DIR_DATA.resolve("canchas.csv"); // <-- NUEVO ARCHIVO

    public GestionArchivos() {
        try {
            if (!Files.exists(DIR_DATA)) Files.createDirectories(DIR_DATA);
            if (!Files.exists(FILE_SOCIOS)) Files.createFile(FILE_SOCIOS);
            if (!Files.exists(FILE_RESERVAS)) Files.createFile(FILE_RESERVAS);
            if (!Files.exists(FILE_CANCHAS)) Files.createFile(FILE_CANCHAS); // <-- NUEVO ARCHIVO
        } catch (IOException e) {
            System.err.println("No se pudo preparar la carpeta/archivos de datos: " + e.getMessage());
        }
    }
    
        public void cargarCanchas(SistemaGestion sistema) {
        try {
            List<String> lines = Files.readAllLines(FILE_CANCHAS, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 2) continue; // id,nombre
                Cancha c = new Cancha(Integer.parseInt(p[0].trim()), p[1].trim());
                sistema.agregarCancha(c);
            }
        } catch (IOException e) {
            System.err.println("Error leyendo canchas.csv: " + e.getMessage());
        }
    }

    // ===================== CARGA =====================

    /** Carga socios desde CSV y los registra en el sistema. */
    public void cargarSocios(SistemaGestion sistema) {
        try {
            List<String> lines = Files.readAllLines(FILE_SOCIOS, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 3) continue; // rut,nombre,telefono
                String rut = p[0].trim();
                String nombre = p[1].trim();
                String tel = p[2].trim();
                sistema.agregarOActualizarSocio(new Socio(rut, nombre, tel));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo socios.csv: " + e.getMessage());
        }
    }

    /** Carga reservas desde CSV; agrega a cancha y asocia a socio si existe. */
    public void cargarReservas(SistemaGestion sistema) {
        try {
            List<String> lines = Files.readAllLines(FILE_RESERVAS, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 5) continue; // id,canchaId,rut,fecha,bloque
                try {
                    int idReserva = Integer.parseInt(p[0].trim());
                    int canchaId  = Integer.parseInt(p[1].trim());
                    String rut    = p[2].trim();
                    LocalDate fecha = LocalDate.parse(p[3].trim()); // ISO yyyy-MM-dd
                    BloqueHorario bloque = BloqueHorario.valueOf(p[4].trim());

                    Cancha cancha = sistema.getCancha(canchaId);
                    if (cancha == null) {
                        System.err.println("Cancha " + canchaId + " no existe; se omite reserva #" + idReserva);
                        continue;
                    }
                    Reserva r = new Reserva(idReserva, canchaId, rut, fecha, bloque);
                    cancha.agregarReserva(r);

                    Socio s = sistema.getSocioByRut(rut);
                    if (s != null) s.agregarReserva(r);
                } catch (Exception ex) {
                    System.err.println("Reserva inválida en CSV: \"" + line + "\" -> " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo reservas.csv: " + e.getMessage());
        }
    }

    // ===================== GUARDADO / APPEND =====================

    /** Agrega una reserva al CSV (append). */
    public void agregarReservaACSV(Reserva r) {
        String row = String.join(",",
                String.valueOf(r.getIdReserva()),
                String.valueOf(r.getCanchaId()),
                r.getRutSocio(),
                r.getFecha().toString(),           // ISO yyyy-MM-dd
                r.getBloque().name()               // Enum exacto
        );
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_RESERVAS, StandardCharsets.UTF_8,
                StandardOpenOption.APPEND)) {
            bw.write(row);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("No se pudo escribir reservas.csv: " + e.getMessage());
        }
    }
    
    public void guardarCanchas(SistemaGestion sistema) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_CANCHAS, StandardCharsets.UTF_8)) {
            for (Cancha c : sistema.getListaCanchas()) {
                String row = String.join(",", String.valueOf(c.getId()), c.getNombre());
                bw.write(row);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar canchas.csv: " + e.getMessage());
        }
    }

    /** Guarda TODOS los socios sobrescribiendo el archivo. */
    public void guardarSocios(SistemaGestion sistema) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_SOCIOS, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Socio s : sistema.getSocios()) {
                String row = String.join(",",
                        s.getRut(),
                        safe(s.getNombre()),
                        safe(s.getTelefono()));
                bw.write(row);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar socios.csv: " + e.getMessage());
        }
    }

    /**
     * Guarda TODAS las reservas sobrescribiendo el archivo.
     * Útil después de eliminar/editar.
     */
    public void guardarReservas(SistemaGestion sistema) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_RESERVAS, StandardCharsets.UTF_8,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            for (Cancha c : sistema.getListaCanchas()) {
                for (Reserva r : c.getReservas()) {
                    String row = String.join(",",
                            String.valueOf(r.getIdReserva()),
                            String.valueOf(r.getCanchaId()),
                            r.getRutSocio(),
                            r.getFecha().toString(),
                            r.getBloque().name());
                    bw.write(row);
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar reservas.csv: " + e.getMessage());
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " "); // evita romper CSV con comas
    }
}


