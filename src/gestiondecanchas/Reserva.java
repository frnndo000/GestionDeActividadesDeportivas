/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Fernando
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Fernando
 */

import java.time.LocalDate;

public class Reserva {
    // Atributos
    private int idReserva ;
    private String rutSocio ;
    private LocalDate fecha ;
    private BloqueHorario bloque ;
    
    // Constructor
    public Reserva(int idReserva, String rutSocio, LocalDate fecha, BloqueHorario bloque) {
        this.idReserva = idReserva;
        this.rutSocio = rutSocio;
        this.fecha = fecha;
        this.bloque = bloque;
    }
    
    // Getters y Setters
    public int getIdReserva() { return idReserva; }
    public void setIdReserva(int idReserva) { this.idReserva = idReserva; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public BloqueHorario getBloque() { return bloque; }
    public void setBloque(BloqueHorario bloque) { this.bloque = bloque; }
    public String getRutSocio() { return rutSocio; }
    public void setRutSocio(String rutSocio) { this.rutSocio = rutSocio; }
}

