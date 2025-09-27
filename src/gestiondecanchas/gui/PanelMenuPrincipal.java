package gestiondecanchas.gui;

import gestiondecanchas.GestionArchivos;
import gestiondecanchas.SistemaGestion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PanelMenuPrincipal extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;

    public PanelMenuPrincipal(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel titulo = new JLabel("Sistema de Reservas", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));

        JButton btnSocios    = new JButton("Socios");
        JButton btnCanchas   = new JButton("Canchas");
        JButton btnReservasN = new JButton("Nueva Reserva");
        JButton btnReservasA = new JButton("Administrar Reservas");
        JButton btnReporte   = new JButton("Generar Reporte"); 

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        add(titulo, gbc);

        gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = 0; gbc.gridy = 1; add(btnSocios, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(btnCanchas, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(btnReservasN, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(btnReservasA, gbc);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; add(btnReporte, gbc);

        btnSocios.addActionListener(e -> ventana.cambiarPanel(new SociosPanel(ventana, sistema)));
        btnCanchas.addActionListener(e -> ventana.cambiarPanel(new CanchasPanel(ventana, sistema)));
        btnReservasN.addActionListener(e -> ventana.cambiarPanel(new PanelReserva(ventana, sistema)));
        btnReservasA.addActionListener(e -> ventana.cambiarPanel(new PanelReservas(ventana, sistema)));
        
        btnReporte.addActionListener(e -> {
            GestionArchivos ga = new GestionArchivos();
            boolean exito = ga.generarReporteReservas(sistema);

            if (exito) {
                JOptionPane.showMessageDialog(this,
                    "Reporte 'reporte_reservas.txt' generado con éxito en la carpeta del proyecto.",
                    "Reporte Generado",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Ocurrió un error al generar el reporte. Revise la consola.",
                    "Error de Reporte",
                    JOptionPane.ERROR_MESSAGE);
            }
    });
    }
}
