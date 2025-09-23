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
            System.out.print("Seleccione una opción: ");
            
            String input = leer.readLine();
            
            // Validar que no esté vacío
            if (input.isEmpty()) {
                System.out.println("Error: Debe ingresar una opción.");
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
                        System.out.println("Opción inválida. Intente de nuevo.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido.");
            }
        }        
    }
    
    public static void hacerReserva(SistemaGestion sistema, BufferedReader leer) throws IOException {
        // Datos del socio
        System.out.print("Ingrese su RUT(sin puntos y sin guion): ");
        String rutSocio = leer.readLine();
        if (rutSocio.isEmpty()) {
            System.out.println("Error: Debe ingresar un RUT.");
            return;
        }
        
        System.out.print("Ingrese su nombre: ");
        String nombre = leer.readLine();
        if (nombre.isEmpty()) {
            System.out.println("Error: Debe ingresar un nombre.");
            return;
        }
        
        System.out.print("Ingrese su teléfono: ");
        String telefono = leer.readLine();
        // El teléfono puede estar vacío según tu constructor de Socio
        
        Socio socio = new Socio(rutSocio, nombre, telefono);
        
        // Selección del día
        System.out.println("\n=== SELECCIÓN DEL DÍA ===");
        System.out.println("1. Lunes");
        System.out.println("2. Martes");
        System.out.println("3. Miércoles");
        System.out.println("4. Jueves");
        System.out.println("5. Viernes");
        System.out.println("6. Sábado");
        System.out.println("7. Domingo");
        System.out.print("Seleccione el día: ");
        
        String inputDia = leer.readLine();
        if (inputDia.isEmpty()) {
            System.out.println("Error: Debe ingresar un día.");
            return;
        }
        
        try {
            int opcionDia = Integer.parseInt(inputDia);
            if (opcionDia < 1 || opcionDia > 7) {
                System.out.println("Día inválido.");
                return;
            }
            
            LocalDate fechaSeleccionada = obtenerProximoDiaSemana(DayOfWeek.of(opcionDia));
            
            // Mostrar horarios disponibles
            mostrarHorariosDisponibles(sistema, fechaSeleccionada);
            
            // Selección de horario
            System.out.print("\nSeleccione el horario (1-8): ");
            String inputHorario = leer.readLine();
            if (inputHorario.isEmpty()) {
                System.out.println("Error: Debes ingresar un horario.");
                return;
            }
            
            try {
                int opcionHorario = Integer.parseInt(inputHorario);
                if (opcionHorario < 1 || opcionHorario > 8) {
                    System.out.println("Horario inválido.");
                    return;
                }
                
                BloqueHorario bloqueSeleccionado = BloqueHorario.values()[opcionHorario - 1];
                
                // Selección de cancha
                System.out.println("\n=== SELECCIÓN DE CANCHA ===");
                System.out.println("Canchas disponibles:");
                
                for (Cancha cancha : sistema.getCanchas()) {
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
                    Cancha canchaElegida = sistema.getCanchaById(idCanchaSeleccionada);
                    if (canchaElegida == null) {
                        System.out.println("Cancha no encontrada.");
                        return;
                    }
                    
                    // Verificar disponibilidad final
                    if (!canchaElegida.estaDisponible(fechaSeleccionada, bloqueSeleccionado)) {
                        System.out.println("La cancha no está disponible en el horario seleccionado.");
                        return;
                    }
                    
                    // Crear y agregar la reserva
                    try {
                        int nuevoId = canchaElegida.getReservas().size() + 1;
                        Reserva nuevaReserva = new Reserva(nuevoId, rutSocio, fechaSeleccionada, bloqueSeleccionado);
                        canchaElegida.agregarReserva(nuevaReserva);
                        
                        System.out.println("\n✅ RESERVA CONFIRMADA");
                        System.out.println("Cancha: " + canchaElegida.getNombre());
                        System.out.println("Fecha: " + fechaSeleccionada);
                        System.out.println("Horario: " + bloqueSeleccionado.getDescripcion());
                        System.out.println("Socio: " + nombre + " (RUT: " + rutSocio + ")");
                        
                    } catch (IllegalStateException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    
                } catch (NumberFormatException e) {
                    System.out.println("Error: Debe ingresar un número válido para la cancha.");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Error: Debe ingresar un número válido para el horario.");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("Error: Debe ingresar un número válido para el día.");
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
            
            // Verificar disponibilidad en alguna cancha
            boolean disponible = false;
            for (Cancha cancha : sistema.getCanchas()) {
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

