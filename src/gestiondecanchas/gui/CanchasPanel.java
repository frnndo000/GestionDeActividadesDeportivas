package gestiondecanchas.gui;

import gestiondecanchas.Cancha;
import gestiondecanchas.GestionArchivos;
import gestiondecanchas.SistemaGestion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CanchasPanel extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;
    private final CanchaTableModel model;
    private final JTable tabla;

    public CanchasPanel(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNueva = new JButton("Nueva Cancha");
        JButton btnEditar = new JButton("Editar Nombre");
        JButton btnEliminar = new JButton("Eliminar Cancha");
        JButton btnVolver = new JButton("Volver");

        top.add(btnNueva);
        top.add(btnEditar);
        top.add(btnEliminar);
        top.add(btnVolver);

        model = new CanchaTableModel(sistema.getListaCanchas());
        tabla = new JTable(model);

        btnNueva.addActionListener(e -> {
            String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva cancha:", "Nueva Cancha", JOptionPane.PLAIN_MESSAGE);
            if (nombre != null && !nombre.trim().isEmpty()) {
                int nuevoId = sistema.getProximoIdCancha();
                Cancha nuevaCancha = new Cancha(nuevoId, nombre.trim());
                sistema.agregarCancha(nuevaCancha);
                new GestionArchivos().guardarCanchas(sistema);
                model.setData(sistema.getListaCanchas());
            }
        });

        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una cancha para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cancha canchaAEditar = model.getAt(row);
            String nuevoNombre = JOptionPane.showInputDialog(this, "Ingrese el nuevo nombre para '" + canchaAEditar.getNombre() + "':", "Editar Cancha", JOptionPane.PLAIN_MESSAGE);
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                canchaAEditar.setNombre(nuevoNombre.trim());
                new GestionArchivos().guardarCanchas(sistema);
                model.refresh();
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una cancha para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Cancha canchaAEliminar = model.getAt(row);
            int conf = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro de que desea eliminar la cancha '" + canchaAEliminar.getNombre() + "'?\n¡Todas las reservas asociadas se perderán!",
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (conf == JOptionPane.YES_OPTION) {
                sistema.eliminarCancha(canchaAEliminar.getId());
                new GestionArchivos().guardarCanchas(sistema);
                new GestionArchivos().guardarReservas(sistema);

                model.setData(sistema.getListaCanchas());
            }
        });

        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
