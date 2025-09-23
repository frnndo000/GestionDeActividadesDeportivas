import java.time.LocalDate;
import java.time.DayOfWeek;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GestionDeCanchas {
    public static void main(String[] args) throws IOException {
        SistemaGestion miSistema = new SistemaGestion();
        BufferedReader leer = new BufferedReader(new InputStreamReader(System.in));
        
        boolean activo = true;
        
        while(activo) {
            System.out.println("\n=== MENU PRINCIPAL ===");
            System.out.println("1. Reservar");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opcion: ");
            
            String input = leer.readLine();
            
            // Validar que no esté vacío
            if (input.isEmpty()) {
                System.out.println("Error: Debe ingresar una opcion.");
                continue;
            }
            
            try {
                int opcion = Integer.parseInt(input);
                
                switch(opcion) {
                    case 1:
                        hacerReserva(miSistema, leer);
                        break;
                    case 2:
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
    
    public static void hacerReserva(SistemaGestion sistema, BufferedReader leer) throws IOException {

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
            System.out.println("¡Socio registrado con exito!");
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
                        System.out.println("La cancha no está disponible en el horario seleccionado.");
                        return;
                    }
                    
                    try {
                        int nuevoId = canchaElegida.getReservas().size() + 1;
                        Reserva nuevaReserva = new Reserva(nuevoId, rutSocio, fechaSeleccionada, bloqueSeleccionado);
                        canchaElegida.agregarReserva(nuevaReserva);
                        socioParaReserva.agregarReserva(nuevaReserva);
                        
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
}