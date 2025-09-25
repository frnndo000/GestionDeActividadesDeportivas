package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PanelMenuPrincipal extends JPanel {
    public PanelMenuPrincipal(VentanaPrincipal ventana, SistemaGestion sistema) {
        setLayout(new GridLayout(5, 1, 15, 15));
        setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel lblTitulo = new JLabel("Cancha Maestra", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 32));

        JButton btnReservar = new JButton("Realizar una nueva reserva");
        JButton btnVerMisReservas = new JButton("Ver mis reservas");
        JButton btnGestionar = new JButton("Gestionar una reserva (Modificar/Cancelar)");
        JButton btnSalir = new JButton("Salir");

        add(lblTitulo);
        add(btnReservar);
        add(btnVerMisReservas);
        add(btnGestionar);
        add(btnSalir);
        
        btnReservar.addActionListener(e -> {
            ventana.cambiarPanel(new PanelReserva(ventana, sistema));
        });
        
        btnSalir.addActionListener(e -> {
            // Aquí deberías llamar a los métodos de guardado si usaras un sistema "batch"
            System.exit(0);
        });
    }
}

