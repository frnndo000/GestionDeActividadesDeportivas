package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import gestiondecanchas.Socio;
import gestiondecanchas.GestionArchivos;
import gestiondecanchas.gui.SocioForm;
import java.util.Collection;

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

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        JButton btnNuevo = new JButton("Nuevo");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver");

        // --- NUEVOS COMPONENTES PARA EL FILTRO (SIA2.5) ---
        JLabel lblFiltro = new JLabel("Mostrar socios con al menos");
        JTextField txtMinReservas = new JTextField(3);
        JLabel lblFiltroFin = new JLabel("reservas.");
        JButton btnFiltrar = new JButton("Filtrar");
        JButton btnMostrarTodos = new JButton("Mostrar Todos");
        
        top.add(btnNuevo);
        top.add(btnEditar);
        top.add(btnEliminar);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(lblFiltro);
        top.add(txtMinReservas);
        top.add(lblFiltroFin);
        top.add(btnFiltrar);
        top.add(btnMostrarTodos);
        top.add(new JSeparator(SwingConstants.VERTICAL));
        top.add(btnVolver);

        model = new SocioTableModel(sistema.getSocios());
        tabla = new JTable(model);

        btnNuevo.addActionListener(e -> {
            SocioForm dlg = new SocioForm(SwingUtilities.getWindowAncestor(this), (Socio) null);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                sistema.agregarOActualizarSocio(dlg.getSocio());
                new GestionArchivos().guardarSocios(sistema);
                model.setData(sistema.getSocios()); // Actualiza la tabla
            }
        });

        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione un socio para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Socio s = model.getAt(row);
            SocioForm dlg = new SocioForm(SwingUtilities.getWindowAncestor(this), s);
            dlg.setVisible(true);
            if (dlg.isOk()) {
                sistema.agregarOActualizarSocio(dlg.getSocio());
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
        
        btnFiltrar.addActionListener(e -> {
            try {
                int minReservas = Integer.parseInt(txtMinReservas.getText());
                if (minReservas < 0) {
                    JOptionPane.showMessageDialog(this, "El número no puede ser negativo.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                // Llama al nuevo método del sistema y actualiza la tabla con los resultados
                Collection<Socio> sociosFiltrados = sistema.filtrarSociosFrecuentes(minReservas);
                model.setData(sociosFiltrados);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        btnMostrarTodos.addActionListener(e -> {
            // Simplemente vuelve a cargar la tabla con la lista completa de socios
            model.setData(sistema.getSocios());
            txtMinReservas.setText(""); // Limpia el campo de texto
        });

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }
}

