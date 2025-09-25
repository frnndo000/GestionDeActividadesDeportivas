package gestiondecanchas.gui;

import gestiondecanchas.Cancha;
import gestiondecanchas.SistemaGestion;

import javax.swing.*;
import java.awt.*;

public class CanchasPanel extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;

    private final CanchaTableModel model;
    private final JTable tabla;

    public CanchasPanel(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnVolver = new JButton("Volver");
        top.add(btnVolver);

        model = new CanchaTableModel(sistema.getListaCanchas());
        tabla = new JTable(model);

        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}
