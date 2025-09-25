package gestiondecanchas.gui;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author et
 */

import gestiondecanchas.SistemaGestion;
import gestiondecanchas.Socio;

import javax.swing.*;
import java.awt.*;

public class SociosPanel extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;
    private final JTable tabla;
    private final SocioTableModel model;

    public SociosPanel(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;
        setLayout(new BorderLayout());

        model = new SocioTableModel(sistema.getSocios());
        tabla = new JTable(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar");
        JButton btnEdit = new JButton("Editar");
        JButton btnDel = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver");
        top.add(btnAdd); top.add(btnEdit); top.add(btnDel); top.add(btnVolver);

        btnAdd.addActionListener(e -> {
            var f = new SocioForm(SwingUtilities.getWindowAncestor(this), null);
            f.setVisible(true);
            if (f.isOk()) {
                sistema.agregarOActualizarSocio(f.getSocio());
                model.setData(sistema.getSocios());
            }
        });

        btnEdit.addActionListener(e -> {
            int row = tabla.getSelectedRow(); if (row < 0) return;
            Socio actual = model.getAt(row);
            var f = new SocioForm(SwingUtilities.getWindowAncestor(this), actual);
            f.setVisible(true);
            if (f.isOk()) {
                sistema.agregarOActualizarSocio(f.getSocio());
                model.setData(sistema.getSocios());
            }
        });

        btnDel.addActionListener(e -> {
            int row = tabla.getSelectedRow(); if (row < 0) return;
            Socio s = model.getAt(row);
            sistema.eliminarSocio(s.getRut());          // <-- método que ya agregaste
            model.setData(sistema.getSocios());
        });

        btnVolver.addActionListener(e ->
            ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema))
        );

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
