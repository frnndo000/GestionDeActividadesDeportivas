package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class VentanaPrincipal extends JFrame {
    private final SistemaGestion sistema;

    public VentanaPrincipal() {
        this.sistema = new SistemaGestion();
        setTitle("Cancha Maestra - Sistema de Reservas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        cambiarPanel(new PanelMenuPrincipal(this, sistema));
    }
    
    public void cambiarPanel(JPanel panel) {
        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new VentanaPrincipal().setVisible(true);
        });
    }
}

