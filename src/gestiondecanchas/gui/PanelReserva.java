package gestiondecanchas.gui;

import gestiondecanchas.*;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.DayOfWeek;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PanelReserva extends JPanel {

    public PanelReserva(VentanaPrincipal ventana, SistemaGestion sistema) {
        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Nueva Reserva", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel lblRut = new JLabel("RUT del Socio:");
        JTextField txtRut = new JTextField(20);

        JLabel lblDia = new JLabel("Día de la Reserva:");
        JComboBox<String> cmbDia = new JComboBox<>(new String[]{
                "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"});

        JLabel lblBloque = new JLabel("Horario:");
        JComboBox<BloqueHorario> cmbBloque = new JComboBox<>(BloqueHorario.values());

        JLabel lblCancha = new JLabel("Cancha:");
        JComboBox<Cancha> cmbCanchas = new JComboBox<>();
        for (Cancha cancha : sistema.getListaCanchas()) cmbCanchas.addItem(cancha);

        JButton btnConfirmar = new JButton("Confirmar Reserva");
        JButton btnVolver = new JButton("Volver al Menú");

        // Posicionamiento
        gbc.gridwidth = 2; gbc.gridx = 0; gbc.gridy = 0; add(lblTitulo, gbc);
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; add(lblRut, gbc);
        gbc.gridx = 1; gbc.gridy = 1; add(txtRut, gbc);
        gbc.gridx = 0; gbc.gridy = 2; add(lblDia, gbc);
        gbc.gridx = 1; gbc.gridy = 2; add(cmbDia, gbc);
        gbc.gridx = 0; gbc.gridy = 3; add(lblBloque, gbc);
        gbc.gridx = 1; gbc.gridy = 3; add(cmbBloque, gbc);
        gbc.gridx = 0; gbc.gridy = 4; add(lblCancha, gbc);
        gbc.gridx = 1; gbc.gridy = 4; add(cmbCanchas, gbc);

        gbc.gridy = 5;
        gbc.gridx = 1; add(btnConfirmar, gbc);
        gbc.gridx = 0; add(btnVolver, gbc);

        // Lógica
        btnConfirmar.addActionListener(e -> {
            String rut = txtRut.getText().trim();
            if (rut.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingresa un RUT.", "Falta dato", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Socio socio = sistema.getSocioByRut(rut);
            if (socio == null) {
                JOptionPane.showMessageDialog(this, "Socio no encontrado. Regístrese o verifique el RUT.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Cancha canchaSeleccionada = (Cancha) cmbCanchas.getSelectedItem();
            DayOfWeek diaSeleccionado = DayOfWeek.of(cmbDia.getSelectedIndex() + 1);
            LocalDate fecha = obtenerProximoDiaSemana(diaSeleccionado);
            BloqueHorario bloqueSeleccionado = (BloqueHorario) cmbBloque.getSelectedItem();

            if (canchaSeleccionada != null && canchaSeleccionada.estaDisponible(fecha, bloqueSeleccionado)) {
                int nuevoId = sistema.getProximoIdReserva();
                Reserva nuevaReserva = new Reserva(nuevoId, canchaSeleccionada.getId(), rut, fecha, bloqueSeleccionado);

                canchaSeleccionada.agregarReserva(nuevaReserva);
                socio.agregarReserva(nuevaReserva);

                // Persistir (estado completo)
                new GestionArchivos().guardarReservas(sistema);

                JOptionPane.showMessageDialog(this, "Reserva creada exitosamente con ID: " + nuevoId);
                ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema));
            } else {
                JOptionPane.showMessageDialog(this, "El horario seleccionado en esa cancha ya está ocupado.", "Error de Disponibilidad", JOptionPane.WARNING_MESSAGE);
            }
        });

        btnVolver.addActionListener(e -> ventana.cambiarPanel(new PanelMenuPrincipal(ventana, sistema)));
    }

    private LocalDate obtenerProximoDiaSemana(DayOfWeek diaSemana) {
        LocalDate hoy = LocalDate.now();
        int diasHastaProximo = diaSemana.getValue() - hoy.getDayOfWeek().getValue();
        if (diasHastaProximo <= 0) diasHastaProximo += 7;
        return hoy.plusDays(diasHastaProximo);
    }
}
