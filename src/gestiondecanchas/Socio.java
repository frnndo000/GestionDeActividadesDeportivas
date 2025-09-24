/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Fernando
 */

package gestiondecanchas;
public class Socio{
    // Atributos
    private String rut ;
    private String nombre ;
    private String telefono ;
    
    // Constructores
    public Socio(String rut, String nombre, String telefono) {
        this.rut = rut ;
        this.nombre = nombre ;
        this.telefono = telefono ;   
    }
    
    public Socio(String rut, String nombre) {
        this.rut = rut ;
        this.nombre = nombre ;
        this.telefono = "No especifica" ;   
    }
    
    // Getters y setters
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
