package gestiondecanchas.gui;

import gestiondecanchas.Cancha;
import gestiondecanchas.GestionArchivos;
import gestiondecanchas.SistemaGestion;
import gestiondecanchas.TipoCancha;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CanchasPanel extends JPanel {
    private final VentanaPrincipal ventana;
    private final SistemaGestion sistema;
    private final CanchaTableModel model;
    private final JTable tabla;

    public CanchasPanel(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnNueva = new JButton("Nueva Cancha");
        JButton btnEditar = new JButton("Editar Cancha");
        JButton btnEliminar = new JButton("Eliminar Cancha");
        JButton btnVolver = new JButton("Volver");

        top.add(btnNueva);
        top.add(btnEditar);
        top.add(btnEliminar);
        top.add(btnVolver);

        model = new CanchaTableModel(sistema.getListaCanchas());
        tabla = new JTable(model);

        btnNueva.addActionListener(e -> crearNuevaCancha());

        btnEditar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una cancha para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            editarCancha(row);
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Seleccione una cancha para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            eliminarCancha(row);
        });

        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
    }

    private void crearNuevaCancha() {
        // Diálogo para ingresar nombre
        String nombre = JOptionPane.showInputDialog(this, "Ingrese el nombre de la nueva cancha:", "Nueva Cancha", JOptionPane.PLAIN_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) return;

        // Diálogo para seleccionar tipo
        TipoCancha[] opciones = TipoCancha.values();
        TipoCancha tipoSeleccionado = (TipoCancha) JOptionPane.showInputDialog(
            this,
            "Seleccione el tipo de cancha:",
            "Tipo de Cancha",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );

        if (tipoSeleccionado == null) return;

        // Crear la cancha
        int nuevoId = sistema.getProximoIdCancha();
        Cancha nuevaCancha = new Cancha(nuevoId, nombre.trim(), tipoSeleccionado);
        sistema.agregarCancha(nuevaCancha);
        
        // Guardar y actualizar
        new GestionArchivos().guardarCanchas(sistema);
        model.setData(sistema.getListaCanchas());
        
        JOptionPane.showMessageDialog(this, 
            "Cancha creada exitosamente:\n" + nuevaCancha.toString(), 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void editarCancha(int row) {
    Cancha canchaAEditar = model.getAt(row);
    
    // Diálogo para editar nombre - CORREGIDO
    Object resultadoNombre = JOptionPane.showInputDialog(
        this,
        "Ingrese el nuevo nombre para '" + canchaAEditar.getNombre() + "':", 
        "Editar Cancha", 
        JOptionPane.PLAIN_MESSAGE,
        null,
        null,
        canchaAEditar.getNombre()
    );
    
    // Verificar si el usuario canceló o dejó vacío
    if (resultadoNombre == null || resultadoNombre.toString().trim().isEmpty()) {
        return;
    }
    
    String nuevoNombre = resultadoNombre.toString().trim();

    // Diálogo para editar tipo
    TipoCancha[] opciones = TipoCancha.values();
    TipoCancha nuevoTipo = (TipoCancha) JOptionPane.showInputDialog(
        this,
        "Seleccione el nuevo tipo de cancha:",
        "Tipo de Cancha",
        JOptionPane.QUESTION_MESSAGE,
        null,
        opciones,
        canchaAEditar.getTipo()
    );

    if (nuevoTipo == null) return;

    // Actualizar la cancha
    canchaAEditar.setNombre(nuevoNombre);
    canchaAEditar.setTipo(nuevoTipo);
    
    // Guardar y actualizar
    new GestionArchivos().guardarCanchas(sistema);
    model.refresh();
}

    private void eliminarCancha(int row) {
        Cancha canchaAEliminar = model.getAt(row);
        int conf = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar la cancha '" + canchaAEliminar.getNombre() + "'?\n¡Todas las reservas asociadas se perderán!",
                "Confirmar Eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (conf == JOptionPane.YES_OPTION) {
            sistema.eliminarCancha(canchaAEliminar.getId());
            new GestionArchivos().guardarCanchas(sistema);
            new GestionArchivos().guardarReservas(sistema);
            model.setData(sistema.getListaCanchas());
        }
    }
}