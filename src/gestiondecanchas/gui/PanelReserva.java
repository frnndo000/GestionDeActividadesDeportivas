package gestiondecanchas.gui;

import gestiondecanchas.*;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.time.DayOfWeek;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PanelReserva extends JPanel {

    private final JComboBox<String> cmbDia;
    private final JComboBox<BloqueHorario> cmbBloque;
    private final JComboBox<Cancha> cmbCanchas;
    private final SistemaGestion sistema;
    private final VentanaPrincipal ventana;
    private final JTextField txtRut;

    public PanelReserva(VentanaPrincipal ventana, SistemaGestion sistema) {
        this.ventana = ventana;
        this.sistema = sistema;
        
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Componentes ---
        JLabel lblTitulo = new JLabel("Nueva Reserva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel lblRut = new JLabel("RUT del Socio:");
        txtRut = new JTextField(20);
        JLabel lblDia = new JLabel("Día de la Reserva:");
        cmbDia = new JComboBox<>(new String[]{"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"});
        JLabel lblCancha = new JLabel("Cancha:");
        cmbCanchas = new JComboBox<>();
        for (Cancha cancha : sistema.getListaCanchas()) cmbCanchas.addItem(cancha);
        JLabel lblBloque = new JLabel("Horario Disponible:");
        cmbBloque = new JComboBox<>();

        // 🔧 CONFIGURAR EL RENDERER PERSONALIZADO PARA MOSTRAR HORARIOS FORMATEADOS
        cmbBloque.setRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof BloqueHorario) {
                    BloqueHorario bloque = (BloqueHorario) value;
                    setText(bloque.getDescripcion()); // Usar la descripción formateada
                }
                return this;
            }
        });

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
        ActionListener actualizadorDeBloques = e -> actualizarBloquesDisponibles();
        cmbDia.addActionListener(actualizadorDeBloques);
        cmbCanchas.addActionListener(actualizadorDeBloques);
        
        // --- Lógica de Botones ---
        btnConfirmar.addActionListener(e -> confirmarReserva());
        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));

        // --- Carga Inicial ---
        actualizarBloquesDisponibles();
    }

    private void confirmarReserva() {
        String rut = txtRut.getText().trim();
        if (rut.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un RUT.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Buscar o crear socio
        Socio socio = obtenerOCrearSocio(rut);
        if (socio == null) return;

        BloqueHorario bloqueSeleccionado = (BloqueHorario) cmbBloque.getSelectedItem();
        if (bloqueSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "No hay horarios disponibles para la selección actual.", "Sin Horarios", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Cancha canchaSeleccionada = (Cancha) cmbCanchas.getSelectedItem();
        LocalDate fecha = obtenerProximoDiaSemana(DayOfWeek.of(cmbDia.getSelectedIndex() + 1));
        
        if (canchaSeleccionada.estaDisponible(fecha, bloqueSeleccionado)) {
            crearReserva(socio, canchaSeleccionada, fecha, bloqueSeleccionado);
        } else {
            JOptionPane.showMessageDialog(this, "La cancha ya no está disponible en el horario seleccionado.", "No Disponible", JOptionPane.WARNING_MESSAGE);
            actualizarBloquesDisponibles(); // Refrescar disponibilidad
        }
    }

    private Socio obtenerOCrearSocio(String rut) {
        Socio socio = sistema.getSocioByRut(rut);
        
        if (socio == null) {
            // Crear nuevo socio
            SocioForm dialog = new SocioForm(SwingUtilities.getWindowAncestor(this), rut);
            dialog.setVisible(true);
            
            if (dialog.isOk()) {
                socio = dialog.getSocio();
                sistema.agregarOActualizarSocio(socio);
                new GestionArchivos().guardarSocios(sistema);
                JOptionPane.showMessageDialog(this, "Socio registrado exitosamente!", "Nuevo Socio", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Se requiere registrar el socio para hacer reservas.", "Registro Requerido", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        
        return socio;
    }

    private void crearReserva(Socio socio, Cancha cancha, LocalDate fecha, BloqueHorario bloque) {
        try {
            int nuevoId = sistema.getProximoIdReserva();
            Reserva nuevaReserva = new Reserva(nuevoId, cancha.getId(), socio.getRut(), fecha, bloque);
            
            cancha.agregarReserva(nuevaReserva);
            socio.agregarReserva(nuevaReserva);
            
            // Guardar en archivo
            GestionArchivos ga = new GestionArchivos();
            ga.agregarReservaACSV(nuevaReserva);
            
            // Mostrar confirmación
            String mensaje = String.format(
                "Reserva creada exitosamente!\n\n" +
                "Socio: %s (%s)\n" +
                "Cancha: %s\n" +
                "Fecha: %s\n" +
                "Horario: %s\n" +
                "ID Reserva: %d",
                socio.getNombre(), socio.getRut(),
                cancha.getNombre(),
                fecha.toString(),
                bloque.getDescripcion(),
                nuevoId
            );
            
            JOptionPane.showMessageDialog(this, mensaje, "Reserva Confirmada", JOptionPane.INFORMATION_MESSAGE);
            ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema));
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al crear la reserva: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarBloquesDisponibles() {
        Cancha canchaSeleccionada = (Cancha) cmbCanchas.getSelectedItem();
        if (canchaSeleccionada == null) return;

        DayOfWeek diaSeleccionado = DayOfWeek.of(cmbDia.getSelectedIndex() + 1);
        LocalDate fecha = obtenerProximoDiaSemana(diaSeleccionado);

        DefaultComboBoxModel<BloqueHorario> modeloBloques = new DefaultComboBoxModel<>();
        
        for (BloqueHorario bloque : BloqueHorario.values()) {
            if (canchaSeleccionada.estaDisponible(fecha, bloque)) {
                modeloBloques.addElement(bloque);
            }
        }
        
        cmbBloque.setModel(modeloBloques);
        
        // Actualizar label con la fecha seleccionada
        if (cmbBloque.getItemCount() > 0) {
            cmbBloque.setSelectedIndex(0);
        }
    }
    
    private LocalDate obtenerProximoDiaSemana(DayOfWeek diaSemana) {
        LocalDate hoy = LocalDate.now();
        int diasHastaProximo = diaSemana.getValue() - hoy.getDayOfWeek().getValue();
        if (diasHastaProximo <= 0) diasHastaProximo += 7;
        return hoy.plusDays(diasHastaProximo);
    }
}

