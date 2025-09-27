package gestiondecanchas;

/**
 * Excepción que se lanza cuando se intenta realizar una operación
 * con un socio que no se encuentra en el sistema.
 */
public class SocioNoEncontradoException extends Exception {

    public SocioNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}