package gestiondecanchas;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class GestionDeCanchas {
    public static void main(String[] args) throws IOException {
        SistemaGestion miSistema = new SistemaGestion();
        GestionArchivos ga = new GestionArchivos(); 
        BufferedReader leer = new BufferedReader(new InputStreamReader(System.in));
        
        boolean activo = true;
        
        while(activo) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Reservar");
            System.out.println("2. Ver reservas por socio");
            System.out.println("3. Ver ocupacion por cancha");
            System.out.println("4. Cancelar una reserva");
            System.out.println("5. Salir");
            System.out.print("Seleccione una opcion: ");
            
            String input = leer.readLine();
            
            if (input.isEmpty()) {
                System.out.println("Error: Debe ingresar una opcion.");
                continue;
            }
            
            try {
                int opcion = Integer.parseInt(input);
                
                switch(opcion) {
                    case 1:
                        hacerReserva(miSistema, leer, ga);
                        break;
                    case 2: 
                        verMisReservas(miSistema, leer);
                        break;
                    case 3:
                        verOcupacionPorCancha(miSistema, leer);
                        break;
                    case 4:
                        cancelarReserva(miSistema, leer);
                        break;
                    case 5:
                        System.out.println("Saliendo del sistema...");
                        activo = false;
                        break;
                    default:
                        System.out.println("Opcion invalida. Intente de nuevo.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un numero valido.");
            }
        }        
    }
    
    public static void hacerReserva(SistemaGestion sistema, BufferedReader leer, GestionArchivos ga) throws IOException {

        System.out.print("Ingrese su RUT(sin puntos ni guion): ");
        String rutSocio = leer.readLine();
        if (rutSocio.isEmpty()) {
            System.out.println("Error: Debe ingresar un RUT.");
            return;
        }
        
        Socio socioExistente = sistema.getSocioByRut(rutSocio);
        Socio socioParaReserva;
        
        if (socioExistente != null) {
            System.out.println("Bienvenido, " + socioExistente.getNombre() + "!");
            socioParaReserva = socioExistente;
        } else {
            System.out.println("Parece que eres un socio nuevo. Vamos a registrarte.");
            System.out.print("Ingrese su nombre completo: ");
            String nombre = leer.readLine();
            System.out.print("Ingrese su telefono: ");
            String telefono = leer.readLine();
            
            socioParaReserva = new Socio(rutSocio, nombre, telefono);
            sistema.agregarOActualizarSocio(socioParaReserva);
            ga.agregarSocioACSV(socioParaReserva);
            System.out.println("¡Socio registrado y guardado con éxito!");
        }

        System.out.println("\n=== SELECCION DEL DIA ===");
        System.out.println("1. Lunes");
        System.out.println("2. Martes");
        System.out.println("3. Miercoles");
        System.out.println("4. Jueves");
        System.out.println("5. Viernes");
        System.out.println("6. Sabado");
        System.out.println("7. Domingo");
        System.out.print("Seleccione el dia: ");
        
        String inputDia = leer.readLine();
        if (inputDia.isEmpty()) {
            System.out.println("Error: Debe ingresar un dia.");
            return;
        }
        
        try {
            int opcionDia = Integer.parseInt(inputDia);
            if (opcionDia < 1 || opcionDia > 7) {
                System.out.println("Dia invalido.");
                return;
            }
            
            LocalDate fechaSeleccionada = obtenerProximoDiaSemana(DayOfWeek.of(opcionDia));

            mostrarHorariosDisponibles(sistema, fechaSeleccionada);

            System.out.print("\nSeleccione el horario (1-8): ");
            String inputHorario = leer.readLine();
            if (inputHorario.isEmpty()) {
                System.out.println("Error: Debes ingresar un horario.");
                return;
            }
            
            try {
                int opcionHorario = Integer.parseInt(inputHorario);
                if (opcionHorario < 1 || opcionHorario > 8) {
                    System.out.println("Horario invalido.");
                    return;
                }
                
                BloqueHorario bloqueSeleccionado = BloqueHorario.values()[opcionHorario - 1];

                System.out.println("\n=== SELECCION DE CANCHA ===");
                System.out.println("Canchas disponibles:");
                
                for (Cancha cancha : sistema.getListaCanchas()) {
                    boolean disponible = cancha.estaDisponible(fechaSeleccionada, bloqueSeleccionado);
                    String estado = disponible ? "DISPONIBLE" : "OCUPADA";
                    System.out.println(cancha.getId() + ". " + cancha.getNombre() + " - " + estado);
                }
                
                System.out.print("Seleccione la cancha: ");
                String inputCancha = leer.readLine();
                if (inputCancha.isEmpty()) {
                    System.out.println("Error: Debe ingresar una cancha.");
                    return;
                }
                
                try {
                    int idCanchaSeleccionada = Integer.parseInt(inputCancha);
                    Cancha canchaElegida = sistema.getCancha(idCanchaSeleccionada);
                    if (canchaElegida == null) {
                        System.out.println("Cancha no encontrada.");
                        return;
                    }
                    
                    if (!canchaElegida.estaDisponible(fechaSeleccionada, bloqueSeleccionado)) {
                        System.out.println("La cancha no esta disponible en el horario seleccionado.");
                        return;
                    }
                    
                    try {
                        int nuevoId = canchaElegida.getReservas().size() + 1;
                        Reserva nuevaReserva = new Reserva(nuevoId, rutSocio, fechaSeleccionada, bloqueSeleccionado);
                        canchaElegida.agregarReserva(nuevaReserva);
                        socioParaReserva.agregarReserva(nuevaReserva);
                        
                        ga.agregarReservaACSV(nuevaReserva);
                        System.out.println("\n RESERVA CONFIRMADA");
                        System.out.println("Cancha: " + canchaElegida.getNombre());
                        System.out.println("Fecha: " + fechaSeleccionada);
                        System.out.println("Horario: " + bloqueSeleccionado.getDescripcion());
                        System.out.println("Socio: " + socioParaReserva.getNombre() + " (RUT: " + socioParaReserva.getRut() + ")");
                        
                    } catch (IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Error: Debe ingresar un numero valido para la cancha.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un numero valido para el horario.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un numero valido para el dia.");
        }
    }
    
    private static LocalDate obtenerProximoDiaSemana(DayOfWeek diaSemana) {
        LocalDate hoy = LocalDate.now();
        int diasHastaProximo = diaSemana.getValue() - hoy.getDayOfWeek().getValue();
        if (diasHastaProximo < 0) {
            diasHastaProximo += 7;
        }
        return hoy.plusDays(diasHastaProximo);
    }
    
    private static void mostrarHorariosDisponibles(SistemaGestion sistema, LocalDate fecha) {
        System.out.println("\nHorarios disponibles para el " + fecha.getDayOfWeek() + " " + fecha + ":");
        System.out.println("┌─────┬─────────────────┬───────────────┐");
        System.out.println("│ No. │     Horario     │ Disponibilidad│");
        System.out.println("├─────┼─────────────────┼───────────────┤");
        
        BloqueHorario[] bloques = BloqueHorario.values();
        for (int i = 0; i < bloques.length; i++) {
            BloqueHorario bloque = bloques[i];
            
            boolean disponible = false;
            for (Cancha cancha : sistema.getListaCanchas()) {
                if (cancha.estaDisponible(fecha, bloque)) {
                    disponible = true;
                    break;
                }
            }
            
            String dispTexto = disponible ? " DISPONIBLE" : " OCUPADO";
            System.out.printf("│  %d  │ %-15s │ %-13s │\n", 
                             i + 1, bloque.getDescripcion(), dispTexto);
        }
        System.out.println("└─────┴─────────────────┴───────────────┘");
    }

    public static void verOcupacionPorCancha(SistemaGestion sistema, BufferedReader leer) throws IOException {
        System.out.println("\n--- Ver Ocupacion por Cancha ---");

        System.out.println("Por favor, seleccione una cancha para ver sus reservas:");
        for (Cancha c : sistema.getListaCanchas()) {
            System.out.println(c.getId() + ". " + c.getNombre());
        }

        try {
            System.out.print("Ingrese el numero de la cancha: ");
            int idCancha = Integer.parseInt(leer.readLine());
            Cancha canchaSeleccionada = sistema.getCancha(idCancha);

            if (canchaSeleccionada == null) {
                System.out.println("Error: Cancha no encontrada.");
                return;
            }

            List<Reserva> reservasDeLaCancha = canchaSeleccionada.getReservas();

            if (reservasDeLaCancha.isEmpty()) {
                System.out.println("La cancha '" + canchaSeleccionada.getNombre() + "' no tiene reservas registradas.");
                return;
            }

            System.out.println("\n--- Reservas para: " + canchaSeleccionada.getNombre() + " ---");
            for (Reserva r : reservasDeLaCancha) {
                Socio s = sistema.getSocioByRut(r.getRutSocio());
                String nombreSocio = (s != null) ? s.getNombre() : "Socio no encontrado";

                System.out.println("--------------------");
                System.out.println("  ID Reserva: " + r.getIdReserva());
                System.out.println("  Socio: " + nombreSocio + " (RUT: " + r.getRutSocio() + ")");
                System.out.println("  Fecha: " + r.getFecha());
                System.out.println("  Horario: " + r.getBloque().getDescripcion());
            }
            System.out.println("--------------------");

        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un ID numerico valido.");
        }
    }
    
    public static void verMisReservas(SistemaGestion sistema, BufferedReader leer) throws IOException {
        System.out.println("\n--- Mis Reservas ---");
        System.out.print("Ingrese su RUT para ver sus reservas: ");
        String rut = leer.readLine();
        Socio socio = sistema.getSocioByRut(rut);

        if (socio == null || socio.getMisReservas().isEmpty()) {
            System.out.println("No se encontraron reservas para el RUT ingresado.");
            return;
        }

        System.out.println("\nReservas para " + socio.getNombre() + ":");
        for (Reserva r : socio.getMisReservas()) {
            Cancha c = sistema.getCancha(r.getIdReserva());
            System.out.println("--------------------");
            System.out.println("  ID Reserva: " + r.getIdReserva());
            System.out.println("  Cancha: " + (c != null ? c.getNombre() : "Desconocida"));
            System.out.println("  Fecha: " + r.getFecha());
            System.out.println("  Horario: " + r.getBloque().getDescripcion());
        }
        System.out.println("--------------------");
    }
    
    public static void cancelarReserva(SistemaGestion sistema, BufferedReader leer) throws IOException {
        System.out.println("\n--- Cancelar Reserva ---");
        System.out.print("Ingrese su RUT: ");
        String rut = leer.readLine();
        Socio socio = sistema.getSocioByRut(rut);

        if (socio == null || socio.getMisReservas().isEmpty()) {
            System.out.println("No tiene reservas activas para cancelar.");
            return;
        }

        verMisReservas(sistema, leer);
        
        try {
            System.out.print("\nIngrese el ID de la reserva que desea cancelar: ");
            int idParaCancelar = Integer.parseInt(leer.readLine());
            
            Reserva reservaParaCancelar = null;

            for (Reserva r : socio.getMisReservas()) {
                if (r.getIdReserva() == idParaCancelar) {
                    reservaParaCancelar = r;
                    break;
                }
            }

            if (reservaParaCancelar != null) {
                Cancha canchaAsociada = sistema.getCancha(reservaParaCancelar.getIdReserva());
                if (canchaAsociada != null) {
                    socio.cancelarReserva(reservaParaCancelar);
                    canchaAsociada.cancelarReserva(reservaParaCancelar);
                    System.out.println("¡Reserva ID " + idParaCancelar + " cancelada exitosamente!");
                } else {
                    System.out.println("Error: No se encontro la cancha asociada a esta reserva.");
                }
            } else {
                System.out.println("Error: No se encontro una reserva con ese ID en su cuenta.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un ID numerico válido.");
        }
    }
}