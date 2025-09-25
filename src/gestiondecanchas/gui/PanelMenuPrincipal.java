package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Menú principal de la aplicación.
 * Muestra 3 botones para navegar a Socios, Canchas y Reservas.
 */
public class PanelMenuPrincipal extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;

    public PanelMenuPrincipal(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        // Layout básico y padding
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(24, 24, 24, 24));

        // Título
        JLabel titulo = new JLabel("Cancha Maestra - Menú Principal", SwingConstants.CENTER);
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 20f));
        add(titulo, BorderLayout.NORTH);

        // Panel central con los botones
        JPanel botones = new JPanel(new GridLayout(0, 1, 12, 12));
        JButton btnSocios   = new JButton("Gestión de Socios");
        JButton btnCanchas  = new JButton("Gestión de Canchas");
        JButton btnReservas = new JButton("Gestión de Reservas");

        // Tamaño preferido para que se vean parejos
        Dimension d = new Dimension(260, 42);
        btnSocios.setPreferredSize(d);
        btnCanchas.setPreferredSize(d);
        btnReservas.setPreferredSize(d);

        botones.add(btnSocios);
        botones.add(btnCanchas);
        botones.add(btnReservas);

        JPanel center = new JPanel(new GridBagLayout());
        center.add(botones, new GridBagConstraints());
        add(center, BorderLayout.CENTER);

        // Pie (opcional)
        JLabel pie = new JLabel("Seleccione una opción para continuar", SwingConstants.CENTER);
        pie.setBorder(new EmptyBorder(12, 0, 0, 0));
        add(pie, BorderLayout.SOUTH);

        // Navegación: al hacer clic, cambiamos el panel en la ventana principal
        btnSocios.addActionListener(e ->
                ventana.cambiarPanel(new SociosPanel(ventana, sistema))
        );

        btnCanchas.addActionListener(e ->
                ventana.cambiarPanel(new CanchasPanel(ventana, sistema))
        );

        btnReservas.addActionListener(e ->
                ventana.cambiarPanel(new PanelReserva(ventana, sistema))
        );
    }
}

