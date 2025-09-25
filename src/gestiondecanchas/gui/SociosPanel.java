package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import gestiondecanchas.Socio;
import gestiondecanchas.GestionArchivos;

import javax.swing.*;
import java.awt.*;

public class SociosPanel extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;

    private final SocioTableModel model;
    private final JTable tabla;

    public SociosPanel(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver");

        top.add(btnNuevo);
        top.add(btnEditar);
        top.add(btnEliminar);
        top.add(btnVolver);

        model = new SocioTableModel(sistema.getSocios());
        tabla = new JTable(model);

        btnNuevo.addActionListener(e -> {
            SocioForm dlg = new SocioForm(SwingUtilities.getWindowAncestor(this), null);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                sistema.agregarOActualizarSocio(dlg.getSocio());
                // Persistir
                new GestionArchivos().guardarSocios(sistema);
                model.setData(sistema.getSocios());
            }
        });

        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow(); if (row < 0) return;
            Socio s = model.getAt(row);
            SocioForm dlg = new SocioForm(SwingUtilities.getWindowAncestor(this), s);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                sistema.agregarOActualizarSocio(dlg.getSocio());
                // Persistir
                new GestionArchivos().guardarSocios(sistema);
                model.setData(sistema.getSocios());
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow(); if (row < 0) return;
            Socio s = model.getAt(row);
            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar socio " + s.getRut() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;

            sistema.eliminarSocio(s.getRut());
            // Persistir
            new GestionArchivos().guardarSocios(sistema);
            model.setData(sistema.getSocios());
        });

        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
