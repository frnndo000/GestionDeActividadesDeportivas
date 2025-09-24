package gestiondecanchas;

public enum BloqueHorario {
    B15_16("15:00 - 16:00"),
    B16_17("16:00 - 17:00"),
    B17_18("17:00 - 18:00"),
    B18_19("18:00 - 19:00"),
    B19_20("19:00 - 20:00"),
    B20_21("20:00 - 21:00"),
    B21_22("21:00 - 22:00"),
    B22_23("22:00 - 23:00");
    
    private final String descripcion ;
  
    BloqueHorario(String descripcion) {
        this.descripcion = descripcion ;
    }
    
    public String getDescripcion() {
        return descripcion ;
    }
}
