package gestiondecanchas;

/**
 * Excepción que se lanza cuando se intenta crear una reserva que
 * entra en conflicto con una ya existente (misma cancha, fecha y bloque).
 */
public class ReservaConflictException extends Exception {

    public ReservaConflictException(String mensaje) {
        super(mensaje);
    }
}