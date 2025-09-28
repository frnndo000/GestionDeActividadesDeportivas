package gestiondecanchas.gui;

import gestiondecanchas.Socio;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SocioTableModel extends AbstractTableModel {
    private final String[] cols = {"RUT", "Nombre", "Teléfono", "Reservas Totales"};
    private final List<Socio> data = new ArrayList<>();

    public SocioTableModel(Collection<Socio> socios) {
        setData(socios);
    }

    public void setData(Collection<Socio> socios) {
        data.clear();
        if (socios != null) data.addAll(socios);
        fireTableDataChanged();
    }

    public Socio getAt(int r) { return data.get(r); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 0, 1, 2 -> String.class; // RUT, Nombre, Teléfono
            case 3 -> Integer.class; // Reservas Totales
            default -> Object.class;
        };
    }
    @Override public Object getValueAt(int r, int c) {
        Socio s = data.get(r);
        return switch (c) {
            case 0 -> s.getRut();
            case 1 -> s.getNombre();
            case 2 -> s.getTelefono();
            case 3 -> s.getReservas().size();
            default -> "";
        };
    }
}
