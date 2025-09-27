package gestiondecanchas;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GestionArchivos {

    private static final Path DIR_DATA = Paths.get("data");
    private static final Path FILE_SOCIOS = DIR_DATA.resolve("socios.csv");
    private static final Path FILE_RESERVAS = DIR_DATA.resolve("reservas.csv");
    private static final Path FILE_CANCHAS = DIR_DATA.resolve("canchas.csv");

    public GestionArchivos() {
        try {
            if (!Files.exists(DIR_DATA)) Files.createDirectories(DIR_DATA);
            if (!Files.exists(FILE_SOCIOS)) Files.createFile(FILE_SOCIOS);
            if (!Files.exists(FILE_RESERVAS)) Files.createFile(FILE_RESERVAS);
            if (!Files.exists(FILE_CANCHAS)) Files.createFile(FILE_CANCHAS);
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
                if (p.length < 3) continue;
                try {
                    int id = Integer.parseInt(p[0].trim());
                    String nombre = p[1].trim();
                    TipoCancha tipo = TipoCancha.valueOf(p[2].trim());
                    Cancha c = new Cancha(id, nombre, tipo);
                    sistema.agregarCancha(c);
                } catch (IllegalArgumentException e) {
                    System.err.println("Tipo de cancha inválido en CSV: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo canchas.csv: " + e.getMessage());
        }
    }

    public void cargarSocios(SistemaGestion sistema) {
        try {
            List<String> lines = Files.readAllLines(FILE_SOCIOS, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 3) continue;
                String rut = p[0].trim();
                String nombre = p[1].trim();
                String tel = p[2].trim();
                sistema.agregarOActualizarSocio(new Socio(rut, nombre, tel));
            }
        } catch (IOException e) {
            System.err.println("Error leyendo socios.csv: " + e.getMessage());
        }
    }

    public void cargarReservas(SistemaGestion sistema) {
        try {
            List<String> lines = Files.readAllLines(FILE_RESERVAS, StandardCharsets.UTF_8);
            for (String line : lines) {
                if (line.isBlank()) continue;
                String[] p = line.split(",", -1);
                if (p.length < 5) continue;
                try {
                    int idReserva = Integer.parseInt(p[0].trim());
                    int canchaId = Integer.parseInt(p[1].trim());
                    String rut = p[2].trim();
                    LocalDate fecha = LocalDate.parse(p[3].trim());
                    BloqueHorario bloque = BloqueHorario.valueOf(p[4].trim());

                    Cancha cancha = sistema.getCancha(canchaId);
                    if (cancha == null) {
                        System.err.println("Cancha " + canchaId + " no existe; se omite reserva #" + idReserva);
                        continue;
                    }
                    Reserva r = new Reserva(idReserva, canchaId, rut, fecha, bloque);
                    cancha.agregarReserva(r);

                    Socio s = sistema.getSocioByRut(rut); // Ojo, este método ahora puede lanzar excepción
                    if (s != null) s.agregarReserva(r);
                } catch (SocioNoEncontradoException ex) {
                    // Si el socio no se encuentra al cargar, es un estado de datos válido, solo se reporta.
                    System.err.println("Advertencia al cargar: " + ex.getMessage());
                } catch (Exception ex) {
                    System.err.println("Reserva inválida en CSV: \"" + line + "\" -> " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo reservas.csv: " + e.getMessage());
        }
    }

    public void agregarReservaACSV(Reserva r) {
        String row = String.join(",",
                String.valueOf(r.getIdReserva()),
                String.valueOf(r.getCanchaId()),
                r.getRutSocio(),
                r.getFecha().toString(),
                r.getBloque().name()
        );
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_RESERVAS, StandardCharsets.UTF_8, StandardOpenOption.APPEND)) {
            bw.write(row);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("No se pudo escribir reservas.csv: " + e.getMessage());
        }
    }

    public void guardarCanchas(SistemaGestion sistema) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_CANCHAS, StandardCharsets.UTF_8)) {
            for (Cancha c : sistema.getListaCanchas()) {
                String row = String.join(",",
                        String.valueOf(c.getId()),
                        c.getNombre(),
                        c.getTipo().name());
                bw.write(row);
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("No se pudo guardar canchas.csv: " + e.getMessage());
        }
    }

    public void guardarSocios(SistemaGestion sistema) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_SOCIOS, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
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

    public void guardarReservas(SistemaGestion sistema) {
        try (BufferedWriter bw = Files.newBufferedWriter(FILE_RESERVAS, StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING)) {
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

    public boolean generarReporteReservas(SistemaGestion sistema) {
        Path archivoReporte = Paths.get("reporte_reservas.txt");

        try (BufferedWriter bw = Files.newBufferedWriter(archivoReporte, StandardCharsets.UTF_8)) {
            bw.write("======================================================\n");
            bw.write("        REPORTE DE RESERVAS DEL SISTEMA\n");
            bw.write("    Generado el: " + java.time.LocalDateTime.now() + "\n");
            bw.write("======================================================\n\n");

            bw.write("------------------------------------------------------\n");
            bw.write("          LISTADO DETALLADO DE RESERVAS\n");
            bw.write("------------------------------------------------------\n\n");
            
            if (sistema.getTodasLasReservas().isEmpty()) {
                bw.write("No hay reservas registradas en el sistema.\n");
            } else {
                for (Cancha cancha : sistema.getListaCanchas()) {
                    bw.write("--- Cancha: " + cancha.getNombre() + " (" + cancha.getTipo().getNombre() + ") ---\n");
                    if (cancha.getReservas().isEmpty()) {
                        bw.write("   (Sin reservas)\n");
                    } else {
                        cancha.getReservas().sort(Comparator.comparing(Reserva::getFecha));
                        for (Reserva reserva : cancha.getReservas()) {
                            Socio socio = null;
                            try {
                                socio = sistema.getSocioByRut(reserva.getRutSocio());
                            } catch (SocioNoEncontradoException e) {
                                // Ignorar para el reporte
                            }
                            String nombreSocio = (socio != null) ? socio.getNombre() : "Socio no encontrado";
                            String linea = String.format("   - ID: %-4d | Fecha: %s | Horario: %-15s | Socio: %s (%s)",
                                    reserva.getIdReserva(), reserva.getFecha(), reserva.getBloque().getDescripcion(),
                                    nombreSocio, reserva.getRutSocio());
                            bw.write(linea + "\n");
                        }
                    }
                    bw.write("\n");
                }
            }

            bw.write("------------------------------------------------------\n");
            bw.write("            CANCHAS MÁS SOLICITADAS\n");
            bw.write("------------------------------------------------------\n\n");

            List<Cancha> canchasOrdenadas = new ArrayList<>(sistema.getListaCanchas());
            canchasOrdenadas.sort(Comparator.comparingInt((Cancha c) -> c.getReservas().size()).reversed());

            if (canchasOrdenadas.isEmpty()) {
                bw.write("No hay canchas registradas.\n\n");
            } else {
                for (Cancha cancha : canchasOrdenadas) {
                    String linea = String.format("   - %-20s (%-15s) | %d reservas",
                            cancha.getNombre(), cancha.getTipo().getNombre(), cancha.getReservas().size());
                    bw.write(linea + "\n");
                }
                bw.write("\n");
            }

            bw.write("------------------------------------------------------\n");
            bw.write("      SOCIOS FRECUENTES (Más de 10 reservas)\n");
            bw.write("------------------------------------------------------\n\n");

            List<Socio> sociosFrecuentes = sistema.getSocios().stream()
                    .filter(socio -> socio.getReservas().size() > 10)
                    .collect(Collectors.toList());

            if (sociosFrecuentes.isEmpty()) {
                bw.write("No se encontraron socios con más de 10 reservas.\n\n");
            } else {
                for (Socio socio : sociosFrecuentes) {
                    String linea = String.format("   - %-25s (%-15s) | %d reservas",
                            socio.getNombre(), socio.getRut(), socio.getReservas().size());
                    bw.write(linea + "\n");
                }
                bw.write("\n");
            }

            bw.write("===================== FIN REPORTE =====================\n");
            return true;
        } catch (IOException e) {
            System.err.println("Error al generar el reporte de reservas: " + e.getMessage());
            return false;
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace(",", " ");
    }
}