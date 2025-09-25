package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import gestiondecanchas.Cancha;
import gestiondecanchas.Reserva;
import gestiondecanchas.BloqueHorario;
import gestiondecanchas.GestionArchivos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.List;

public class PanelReservas extends JPanel {
    private final VentanaPrincipal frame;
    private final SistemaGestion sistema;

    private final JComboBox<Cancha> cboCancha = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Fecha (YYYY-MM-DD)", "Bloque"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(model);

    public PanelReservas(VentanaPrincipal frame, SistemaGestion sistema) {
        this.frame = frame;
        this.sistema = sistema;
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Cancha:"));
        for (Cancha c : sistema.getListaCanchas()) cboCancha.addItem(c);
        top.add(cboCancha);

        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnEditarFecha = new JButton("Editar Fecha");
        JButton btnEditarBloque = new JButton("Editar Bloque");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver");

        top.add(btnRefrescar);
        top.add(btnEditarFecha);
        top.add(btnEditarBloque);
        top.add(btnEliminar);
        top.add(btnVolver);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // Eventos
        btnRefrescar.addActionListener(e -> cargarTabla());
        cboCancha.addActionListener(e -> cargarTabla());

        btnEditarFecha.addActionListener(e -> {
            int idSel = getIdSeleccionado();
            int canchaId = getCanchaSeleccionadaId();
            if (idSel == -1 || canchaId == -1) return;

            String nueva = JOptionPane.showInputDialog(this, "Nueva fecha (YYYY-MM-DD):");
            if (nueva == null || nueva.isBlank()) return;

            try {
                boolean ok = sistema.editarReservaFecha(canchaId, idSel, LocalDate.parse(nueva.trim()));
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "No se pudo editar.");
                } else {
                    new GestionArchivos().guardarReservas(sistema);
                }
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fecha inválida.");
            }
        });

        btnEditarBloque.addActionListener(e -> {
            int idSel = getIdSeleccionado();
            int canchaId = getCanchaSeleccionadaId();
            if (idSel == -1 || canchaId == -1) return;

            String nuevo = JOptionPane.showInputDialog(this, "Nuevo bloque (ej: B15_16, B16_17, ...):");
            if (nuevo == null || nuevo.isBlank()) return;

            try {
                boolean ok = sistema.editarReservaBloque(canchaId, idSel, BloqueHorario.valueOf(nuevo.trim()));
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "No se pudo editar.");
                } else {
                    new GestionArchivos().guardarReservas(sistema);
                }
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Bloque inválido.");
            }
        });

        btnEliminar.addActionListener(e -> {
            int idSel = getIdSeleccionado();
            int canchaId = getCanchaSeleccionadaId();
            if (idSel == -1 || canchaId == -1) return;

            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar la reserva #" + idSel + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;

            boolean ok = sistema.eliminarReserva(canchaId, idSel);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "No se encontró la reserva.");
            } else {
                new GestionArchivos().guardarReservas(sistema);
            }
            cargarTabla();
        });

        btnVolver.addActionListener(e -> frame.cambiarPanel(new PanelMenuPrincipal(frame, sistema)));

        if (cboCancha.getItemCount() > 0) cboCancha.setSelectedIndex(0);
        cargarTabla();
    }

    private int getCanchaSeleccionadaId() {
        Cancha c = (Cancha) cboCancha.getSelectedItem();
        return (c != null) ? c.getId() : -1;
    }

    private int getIdSeleccionado() {
        int row = tabla.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Selecciona una reserva en la tabla.");
            return -1;
        }
        Object v = model.getValueAt(row, 0);
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido en la tabla.");
            return -1;
        }
    }

    private void cargarTabla() {
        model.setRowCount(0);
        int canchaId = getCanchaSeleccionadaId();
        if (canchaId == -1) return;
        List<Reserva> rs = sistema.listarReservasOrdenadas(canchaId);
        for (Reserva r : rs) model.addRow(new Object[]{ r.getIdReserva(), r.getFecha(), r.getBloque() });
    }
}
