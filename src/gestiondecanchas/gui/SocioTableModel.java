package gestiondecanchas.gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author et
 */
import gestiondecanchas.Socio;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SocioTableModel extends AbstractTableModel {
    private final String[] cols = {"RUT", "Nombre", "Teléfono"};
    private List<Socio> data = new ArrayList<>();

    public SocioTableModel(Collection<Socio> socios) { setData(socios); }
    public void setData(Collection<Socio> socios) { this.data = new ArrayList<>(socios); fireTableDataChanged(); }
    public Socio getAt(int row) { return data.get(row); }

    @Override public int getRowCount() { return data.size(); }
    @Override public int getColumnCount() { return cols.length; }
    @Override public String getColumnName(int c) { return cols[c]; }
    @Override public Object getValueAt(int r, int c) {
        Socio s = data.get(r);
        return switch (c) {
            case 0 -> s.getRut();
            case 1 -> s.getNombre();
            case 2 -> s.getTelefono();
            default -> "";
        };
    }
}

