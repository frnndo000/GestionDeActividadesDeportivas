/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Fernando
 */

import java.util.ArrayList;
import java.util.List;
        
public class SistemaGestion {
    private List<Cancha> canchas ;
    
    public SistemaGestion() {
        this.canchas = new ArrayList<>() ;
        
        this.canchas.add(new Cancha(1, "Cancha 1")) ;
        this.canchas.add(new Cancha(2, "Cancha 2")) ;
    }
    
    public List<Cancha> getCanchas() { return canchas ; }
    
    public Cancha getCanchaById(int id) {
        for (Cancha cancha : canchas) {
            if (cancha.getId() == id) {
                return cancha;
            }
        }
        return null;
    }
}