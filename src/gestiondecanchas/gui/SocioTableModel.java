package gestiondecanchas.gui;

import gestiondecanchas.Socio;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SocioTableModel extends AbstractTableModel {
    // 1. AÑADIMOS LA NUEVA COLUMNA A LA LISTA DE CABECERAS
    private final String[] cols = {"RUT", "Nombre", "Teléfono", "Reservas Totales"};
    private final List<Socio> data = new ArrayList<>();

    public SocioTableModel(Collection<Socio> socios) {
        setData(socios);
    }

    public void setData(Collection<Socio> socios) {
        data.clear();
        if (socios != null) data.addAll(socios);
        fireTableDataChanged(); // Notifica a la tabla que los datos han cambiado
    }

    public Socio getAt(int r) { return data.get(r); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    
    // 2. ACTUALIZAMOS ESTE MÉTODO PARA QUE DEVUELVA EL DATO CORRECTO PARA CADA COLUMNA
    @Override public Object getValueAt(int r, int c) {
        Socio s = data.get(r);
        return switch (c) {
            case 0 -> s.getRut();
            case 1 -> s.getNombre();
            case 2 -> s.getTelefono();
            // Para la columna 3, obtenemos el tamaño de la lista de reservas del socio
            case 3 -> s.getMisReservas().size(); 
            default -> "";
        };
    }
}
