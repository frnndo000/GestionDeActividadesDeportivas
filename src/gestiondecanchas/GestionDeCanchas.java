/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestiondecanchas;

/**
 *
 * @author Fernando
 */
public class GestionDeCanchas {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.out.println("hola");
    }
}

public class Socio {
    // Atributos
    private int rut ;
    private String nombre ;
    private int telefono ;
    
    // Constructores
    public Socio(int rut, String nombre, int telefono) {
        this.rut = rut ;
        this.nombre = nombre ;
        this.telefono = telefono ;   
    }
    
    public Socio(int rut, String nombre) {
        this.rut = rut ;
        this.nombre = nombre ;
        this.telefono = 0 ;   
    }
    
    
    // Getters y setters
    public int getRut() { return rut; }
    public void setRut(int rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public int getTelefono() { return telefono; }
    public void setTelefono(int telefono) { this.telefono = telefono; }
    
}
