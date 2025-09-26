package gestiondecanchas.gui;

import gestiondecanchas.*;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PanelReserva extends JPanel {

    // Declaramos los componentes como atributos de la clase para poder acceder a ellos
    private final JComboBox<String> cmbDia;
    private final JComboBox<BloqueHorario> cmbBloque;
    private final JComboBox<Cancha> cmbCanchas;
    private final SistemaGestion sistema;

    public PanelReserva(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.sistema = sistema; // Guardamos la referencia al sistema
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Componentes ---
        JLabel lblTitulo = new JLabel("Nueva Reserva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel lblRut = new JLabel("RUT del Socio:");
        JTextField txtRut = new JTextField(20);
        JLabel lblDia = new JLabel("Día de la Reserva:");
        cmbDia = new JComboBox<>(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"});
        JLabel lblCancha = new JLabel("Cancha:");
        cmbCanchas = new JComboBox<>();
        for (Cancha cancha : sistema.getListaCanchas()) cmbCanchas.addItem(cancha);
        JLabel lblBloque = new JLabel("Horario Disponible:");
        cmbBloque = new JComboBox<>(); // Lo inicializamos vacío

        JButton btnConfirmar = new JButton("Confirmar Reserva");
        JButton btnVolver = new JButton("Volver al Menú");

        // --- Layout ---
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0; add(lblTitulo, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; add(lblRut, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(txtRut, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblDia, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(cmbDia, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(lblCancha, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(cmbCanchas, gbc);
        gbc.gridx = 0; gbc.gridy = 4; add(lblBloque, gbc);
        gbc.gridx = 1; gbc.gridy = 4; add(cmbBloque, gbc);
        gbc.gridy = 5;
        gbc.gridx = 1; add(btnConfirmar, gbc);
        gbc.gridx = 0; add(btnVolver, gbc);

        // --- LÓGICA DE EVENTOS DINÁMICOS ---
        
        // Creamos un "escuchador" que se activará cuando cambie el día o la cancha
        ActionListener actualizadorDeBloques = e -> actualizarBloquesDisponibles();
        cmbDia.addActionListener(actualizadorDeBloques);
        cmbCanchas.addActionListener(actualizadorDeBloques);
        
        // --- Lógica de Botones ---
        btnConfirmar.addActionListener(e -> {
            // ... (lógica para obtener y validar el socio) ...
            Socio socio = sistema.getSocioByRut(txtRut.getText().trim());
            if (socio == null) { /* ...manejo de socio no encontrado... */ return; }

            BloqueHorario bloqueSeleccionado = (BloqueHorario) cmbBloque.getSelectedItem();
            
            // Si no hay horarios disponibles, el JComboBox estará vacío
            if (bloqueSeleccionado == null) {
                JOptionPane.showMessageDialog(this, "No hay horarios disponibles para la selección actual.", "Sin Horarios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Cancha canchaSeleccionada = (Cancha) cmbCanchas.getSelectedItem();
            LocalDate fecha = obtenerProximoDiaSemana(DayOfWeek.of(cmbDia.getSelectedIndex() + 1));
            
            // Aunque ya filtramos, hacemos una última verificación por si acaso
            if (canchaSeleccionada.estaDisponible(fecha, bloqueSeleccionado)) {
                // ... (lógica para crear y guardar la reserva que ya tenías) ...
                int nuevoId = sistema.getProximoIdReserva();
                Reserva nuevaReserva = new Reserva(nuevoId, canchaSeleccionada.getId(), socio.getRut(), fecha, bloqueSeleccionado);
                canchaSeleccionada.agregarReserva(nuevaReserva);
                socio.agregarReserva(nuevaReserva);
                new GestionArchivos().agregarReservaACSV(nuevaReserva);
                
                JOptionPane.showMessageDialog(this, "Reserva creada exitosamente para " + socio.getNombre());
                ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema));
            }
        });
        
        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));

        // --- Carga Inicial ---
        actualizarBloquesDisponibles(); // Llama al método una vez para la carga inicial
    }

    /**
     * Este método se encarga de actualizar la lista de bloques horarios
     * basándose en el día y la cancha seleccionados.
     */
    private void actualizarBloquesDisponibles() {
        Cancha canchaSeleccionada = (Cancha) cmbCanchas.getSelectedItem();
        if (canchaSeleccionada == null) return;

        DayOfWeek diaSeleccionado = DayOfWeek.of(cmbDia.getSelectedIndex() + 1);
        LocalDate fecha = obtenerProximoDiaSemana(diaSeleccionado);

        // Creamos un modelo nuevo para el JComboBox de bloques
        DefaultComboBoxModel<BloqueHorario> modeloBloques = new DefaultComboBoxModel<>();
        
        // Recorremos todos los bloques posibles y añadimos solo los que están libres
        for (BloqueHorario bloque : BloqueHorario.values()) {
            if (canchaSeleccionada.estaDisponible(fecha, bloque)) {
                modeloBloques.addElement(bloque);
            }
        }
        
        // Asignamos el nuevo modelo al JComboBox
        cmbBloque.setModel(modeloBloques);
    }
    
    private LocalDate obtenerProximoDiaSemana(DayOfWeek diaSemana) {
        LocalDate hoy = LocalDate.now();
        int diasHastaProximo = diaSemana.getValue() - hoy.getDayOfWeek().getValue();
        if (diasHastaProximo <= 0) diasHastaProximo += 7;
        return hoy.plusDays(diasHastaProximo);
    }
}

