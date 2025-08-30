/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestiondecanchas;

import java.io.BufferedReader;
import java.io.IO;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author Fernando
 */
public class GestionDeCanchas {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
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
                    System.out.println("Haga su reserva") ;
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
}