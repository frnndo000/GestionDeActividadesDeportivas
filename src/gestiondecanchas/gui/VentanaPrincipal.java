package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class VentanaPrincipal extends JFrame {
    private final SistemaGestion sistema;

    public VentanaPrincipal() {
        this.sistema = new SistemaGestion();
        setTitle("Sistema de Reservas");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel inicial
        cambiarPanel(new PanelMenuPrincipal(this, sistema));

        // Menú de barra
        setJMenuBar(crearMenuBar());
    }

    private JMenuBar crearMenuBar() {
        JMenuBar bar = new JMenuBar();

        JMenu m = new JMenu("Reservas");

        JMenuItem miAdmin = new JMenuItem("Administrar");
        miAdmin.addActionListener(e -> cambiarPanel(new PanelReservas(this, sistema)));
        m.add(miAdmin);

        JMenuItem miNueva = new JMenuItem("Nueva");
        miNueva.addActionListener(e -> cambiarPanel(new PanelReserva(this, sistema)));
        m.add(miNueva);

        bar.add(m);
        return bar;
    }

    public void cambiarPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VentanaPrincipal().setVisible(true));
    }
}
