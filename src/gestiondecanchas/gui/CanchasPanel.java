/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondecanchas.gui;

/**
 *
 * @author et
 */
import gestiondecanchas.Cancha;
import gestiondecanchas.SistemaGestion;

import javax.swing.*;
import java.awt.*;

public class CanchasPanel extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;
    private final JTable tabla;
    private final CanchaTableModel model;

    public CanchasPanel(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;
        setLayout(new BorderLayout());

        // Usa la lista interna para poder agregar/eliminar (según tu getter)
        model = new CanchaTableModel(sistema.getListaCanchas());
        tabla = new JTable(model);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAdd = new JButton("Agregar");
        JButton btnRename = new JButton("Renombrar");
        JButton btnDel = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver");
        top.add(btnAdd); top.add(btnRename); top.add(btnDel); top.add(btnVolver);

        btnAdd.addActionListener(e -> {
            String idStr = JOptionPane.showInputDialog(this, "ID de cancha:");
            if (idStr == null) return;
            String nombre = JOptionPane.showInputDialog(this, "Nombre:");
            if (nombre == null) return;
            try {
                int id = Integer.parseInt(idStr.trim());
                if (sistema.getCancha(id) != null) {
                    JOptionPane.showMessageDialog(this, "ID ya existe."); return;
                }
                sistema.getListaCanchas().add(new Cancha(id, nombre));
                model.refresh();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.");
            }
        });

        btnRename.addActionListener(e -> {
            int row = tabla.getSelectedRow(); if (row < 0) return;
            var c = model.getAt(row);
            String nuevo = JOptionPane.showInputDialog(this, "Nuevo nombre:", c.getNombre());
            if (nuevo != null && !nuevo.isBlank()) {
                c.setNombre(nuevo);
                model.refresh();
            }
        });

        btnDel.addActionListener(e -> {
            int row = tabla.getSelectedRow(); if (row < 0) return;
            var c = model.getAt(row);
            sistema.getListaCanchas().remove(c);
            model.refresh();
        });

        btnVolver.addActionListener(e ->
            ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema))
        );

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
