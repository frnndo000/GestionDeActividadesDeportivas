/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


import java.time.LocalDate;
import java.time.DayOfWeek;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Fernando
 */
public class GestionDeCanchas {
    public static void main(String[] args) throws IOException {
        SistemaGestion miSistema = new SistemaGestion() ;
        BufferedReader leer = new BufferedReader(new InputStreamReader(System.in)) ;
        
        boolean activo = true ;
        
        System.out.println("MENU PRINCIPAL") ;
        System.out.println("Seleccione una opcion") ;
        System.out.println("1. Reservar") ;
        System.out.println("2. Salir") ;
        
        while(activo) {
            int opcion = Integer.parseInt(leer.readLine()) ;
            switch(opcion) {
                case 1 :
                    hacerReserva(miSistema, leer) ;
                    break ;
                case 2 :
                    System.out.println("Saliendo del sistema") ;
                    activo = false ;
                    break ;
                default:
                    System.out.println("Opcion invalida. Intente de nuevo.");
                    break;
            }
        }        
    }
    
    public static void hacerReserva(SistemaGestion sistema, BufferedReader leer) throws IOException {
    System.out.println("Ingrese su RUT:");
    String rutSocio = leer.readLine();
    System.out.println("Ingrese su nombre:");
    String nombre = leer.readLine();
    System.out.println("Ingrese su telefono:");
    String telefono = leer.readLine();
    Socio socio = new Socio(rutSocio, nombre, telefono); ///hay que cambiarlo y añadirlo con la clase socio q no esta todavia (creo)
    
    System.out.println("Seleccione el día de la semana para la reserva:");
    System.out.println("1. Lunes");
    System.out.println("2. Martes");
    System.out.println("3. Miércoles");
    System.out.println("4. Jueves");
    System.out.println("5. Viernes");
    System.out.println("6. Sábado");
    System.out.println("7. Domingo");

    int opcionDia = Integer.parseInt(leer.readLine());
    if (opcionDia < 1 || opcionDia > 7) {
        System.out.println("Día inválido.");
        return;
    }
    LocalDate fechaSeleccionada = obtenerProximoDiaSemana(DayOfWeek.of(opcionDia));
    
    mostrarHorariosDisponibles(sistema, fechaSeleccionada);
    
    System.out.println("Seleccione el horario (1-8):");
        int opcionHorario = Integer.parseInt(leer.readLine());
        
        if (opcionHorario < 1 || opcionHorario > 8) {
            System.out.println("Horario inválido.");
            return;
        }
    
    BloqueHorario bloqueSeleccionado = BloqueHorario.values()[opcionHorario - 1];
    
    System.out.println("Paso 2: Elija una cancha:") ;
    for(Cancha cancha : sistema.getCanchas()) { 
        boolean disponible = cancha.estaDisponible(fechaSeleccionada, bloqueSeleccionado);
        String estado  = disponible ? "DISPONIBLE" : "OCUPADA";
        System.out.println(cancha.getId() + ". " + cancha.getNombre() + " - " + estado);
    }
    
    int idCanchaSeleccionada = Integer.parseInt(leer.readLine()) ;
        
    Cancha canchaElegida = sistema.getCanchaById(idCanchaSeleccionada);
        if (canchaElegida == null) {
            System.out.println("Cancha no encontrada.");
            return;
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
        System.out.println("Horarios disponibles para el " + fecha.getDayOfWeek() + " " + fecha + ":");
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

