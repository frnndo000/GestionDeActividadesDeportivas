/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Fernando
 */

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cancha {
    private int id ;
    private String nombre ;
    private List<Reserva> reservas ;
    
    public Cancha(int id, String nombre) {
        this.id = id ;
        this.nombre = nombre ;
        this.reservas = new ArrayList<>() ;
    }
    
    public int getId() { return id ; }
    public void setId(int nuevoId) { this.id = nuevoId ; }
    public String getNombre() { return nombre ; }
    public void setNombre(String nuevoNombre) { this.nombre = nuevoNombre ; }
}