package gestiondecanchas;

/**
 * Enum de bloques horarios. Usa estos nombres exactos en tu código y CSV.
 * Ejemplo: B08_09 corresponde al bloque 08:00 - 09:00
 */
public enum BloqueHorario {
    B08_09, B09_10, B10_11, B11_12, B12_13, B13_14, B14_15,
    B15_16, B16_17, B17_18, B18_19, B19_20, B20_21, B21_22;

    /** Devuelve una descripción amigable del bloque. */
    public String getDescripcion() {
        // Convierte "B08_09" en "08:00 - 09:00"
        String s = name().substring(1); // Ej: "08_09"
        String[] p = s.split("_");
        if (p.length == 2) {
            return p[0] + ":00 - " + p[1] + ":00";
        }
        return name();
    }
}

