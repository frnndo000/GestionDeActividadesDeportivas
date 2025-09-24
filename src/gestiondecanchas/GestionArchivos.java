package gestiondecanchas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class GestionArchivos {
    private static final String ARCHIVO_SOCIOS = "socios.csv";
    private static final String ARCHIVO_RESERVAS = "reservas.csv";
    
    public void cargarSocios(SistemaGestion sistema) {
        try (BufferedReader leer = new BufferedReader(new FileReader(ARCHIVO_SOCIOS))) {
            String linea;
            while ((linea = leer.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 3) {
                    Socio socio = new Socio(datos[0], datos[1], datos[2]);
                    sistema.agregarOActualizarSocio(socio);
                }
            }
            System.out.println("Socios cargados exitosamente desde " + ARCHIVO_SOCIOS);
        } catch (IOException e) {
            System.out.println("No se encontro el archivo de socios. Se iniciara sin datos previos.");
        }
    }
    
    public void cargarReservas(SistemaGestion sistema) {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_RESERVAS))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(",");
                if (datos.length == 5) {
                    int idReserva = Integer.parseInt(datos[0]);
                    String rutSocio = datos[1];
                    int idCancha = Integer.parseInt(datos[2]);
                    LocalDate fecha = LocalDate.parse(datos[3]);
                    BloqueHorario bloque = BloqueHorario.valueOf(datos[4]);

                    Cancha cancha = sistema.getCancha(idCancha);
                    Socio socio = sistema.getSocioByRut(rutSocio);

                    if (cancha != null && socio != null) {
                        Reserva reserva = new Reserva(idReserva, idCancha, rutSocio , fecha, bloque);
                        cancha.agregarReservaDesdeArchivo(reserva);
                        socio.agregarReserva(reserva);
                    }
                }
            }
            System.out.println(">> Reservas cargadas exitosamente desde " + ARCHIVO_RESERVAS);
        } catch (IOException e) {
            System.out.println(">> No se encontro el archivo de reservas. Se iniciara sin reservas previas.");
        }
    }
    
    public void agregarSocioACSV(Socio socio) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_SOCIOS, true))) {
            pw.println(socio.getRut() + "," + socio.getNombre() + "," + socio.getTelefono());
        } catch (IOException e) {
            System.err.println("Error al agregar socio al archivo: " + e.getMessage());
        }
    }
    
    public void agregarReservaACSV(Reserva reserva) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS, true))) {
            pw.println(reserva.getIdReserva() + "," +
                       reserva.getRutSocio() + "," +
                       reserva.getIdCancha() + "," +
                       reserva.getFecha() + "," +
                       reserva.getBloque().name());
        } catch (IOException e) {
            System.err.println("Error al agregar reserva al archivo: " + e.getMessage());
        }
    }
    
    public void actualizarArchivoReservas(SistemaGestion sistema) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO_RESERVAS, false))) {
            for (Cancha cancha : sistema.getListaCanchas()) {
                for (Reserva reserva : cancha.getReservas()) {
                    pw.println(reserva.getIdReserva() + "," +
                               reserva.getRutSocio() + "," +
                               reserva.getIdCancha() + "," +
                               reserva.getFecha() + "," +
                               reserva.getBloque().name());
                }
            }
        } catch (IOException e) {
            System.err.println("Error al actualizar reservas.csv: " + e.getMessage());
        }
    }
}
