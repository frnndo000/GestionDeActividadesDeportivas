package gestiondecanchas.gui;

import gestiondecanchas.Cancha;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class CanchaTableModel extends AbstractTableModel {
    private final String[] cols = {"ID", "Nombre"};
    private final List<Cancha> data;

    public CanchaTableModel(List<Cancha> data) { this.data = data; }
    public void refresh() { fireTableDataChanged(); }
    public Cancha getAt(int r) { return data.get(r); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public Object getValueAt(int r, int c) {
        Cancha x = data.get(r);
        return c==0 ? x.getId() : x.getNombre();
    }
}

