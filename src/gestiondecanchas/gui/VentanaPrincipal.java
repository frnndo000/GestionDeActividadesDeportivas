package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    private final SistemaGestion sistema;

    public VentanaPrincipal() {
        this.sistema = new SistemaGestion();
        setTitle("Sistema de Reservas - Versión Gráfica");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cambiarPanel(new PanelMenuPrincipal(this, sistema));
        setJMenuBar(crearMenuBar());
    }

    private JMenuBar crearMenuBar() {
        JMenuBar bar = new JMenuBar();
        JMenu menuReservas = new JMenu("Reservas");
        
        JMenuItem itemAdmin = new JMenuItem("Administrar");
        itemAdmin.addActionListener(e -> cambiarPanel(new PanelReservas(this, sistema)));
        
        JMenuItem itemNueva = new JMenuItem("Nueva");
        itemNueva.addActionListener(e -> cambiarPanel(new PanelReserva(this, sistema)));
        
        menuReservas.add(itemAdmin);
        menuReservas.add(itemNueva);
        bar.add(menuReservas);
        
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
