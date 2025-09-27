/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondecanchas;

public enum TipoCancha {
    MULTICANCHA("Multicancha"),
    FUTBOL_PASTO("Cancha de Fútbol");

    private final String nombre;

    TipoCancha(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() { 
        return nombre; 
    }

    @Override
    public String toString() {
        return nombre;
    }
}
