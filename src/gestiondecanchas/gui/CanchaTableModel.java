package gestiondecanchas.gui;

import gestiondecanchas.Cancha;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class CanchaTableModel extends AbstractTableModel {
    private final String[] columnNames = {"ID", "Nombre", "Tipo"};  // Columna añadida
    private List<Cancha> canchas;

    public CanchaTableModel(List<Cancha> canchas) {
        this.canchas = new ArrayList<>(canchas);
    }

    @Override
    public int getRowCount() {
        return canchas.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Cancha cancha = canchas.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return cancha.getId();
            case 1:
                return cancha.getNombre();
            case 2:  // Nueva columna para el tipo
                return cancha.getTipo().getNombre();
            default:
                return null;
        }
    }

    // --- MÉTODOS PARA ACTUALIZAR LA TABLA ---
    public void setData(List<Cancha> nuevasCanchas) {
        this.canchas = new ArrayList<>(nuevasCanchas);
        fireTableDataChanged();
    }
    
    public Cancha getAt(int row) {
        return canchas.get(row);
    }
    
    public void refresh() {
        fireTableDataChanged();
    }
}