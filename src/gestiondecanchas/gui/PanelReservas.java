package gestiondecanchas.gui;

import gestiondecanchas.SistemaGestion;
import gestiondecanchas.Cancha;
import gestiondecanchas.Reserva;
import gestiondecanchas.Socio;
import gestiondecanchas.SocioNoEncontradoException;
import gestiondecanchas.BloqueHorario;
import gestiondecanchas.GestionArchivos;
import gestiondecanchas.TipoCancha;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PanelReservas extends JPanel {
    private final VentanaPrincipal frame;
    private final SistemaGestion sistema;

    private final JComboBox<Cancha> cboCancha = new JComboBox<>();
    // -> CAMBIO: Añadir la columna "Cancha" al final.
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "RUT Socio", "Nombre Socio", "Teléfono", "Fecha", "Hora", "Cancha"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable tabla = new JTable(model);
    
    // -> CAMBIO: Guardar la lista de reservas mostradas para que los botones de editar/eliminar funcionen correctamente.
    private List<Reserva> reservasMostradas;

    public PanelReservas(VentanaPrincipal frame, SistemaGestion sistema) {
        this.frame = frame;
        this.sistema = sistema;
        this.reservasMostradas = new ArrayList<>();
        setLayout(new BorderLayout(10,10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Cancha:"));

        // -> CAMBIO: Añadir la opción "TODAS LAS CANCHAS" al principio del ComboBox.
        // Se llama al constructor de 3 argumentos con un tipo por defecto.
        cboCancha.addItem(new Cancha(-1, "--- TODAS LAS CANCHAS ---", TipoCancha.MULTICANCHA));
        for (Cancha c : sistema.getListaCanchas()) {
            cboCancha.addItem(c);
        }
        top.add(cboCancha);

        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnEditarFecha = new JButton("Editar Fecha");
        JButton btnEditarBloque = new JButton("Editar Bloque");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnVolver = new JButton("Volver");

        top.add(btnRefrescar);
        top.add(btnEditarFecha);
        top.add(btnEditarBloque);
        top.add(btnEliminar);
        top.add(btnVolver);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);

        // --- Eventos ---
        btnRefrescar.addActionListener(e -> cargarTabla());
        cboCancha.addActionListener(e -> cargarTabla());

        // -> CAMBIO: La lógica de los botones ahora obtiene el ID de la cancha desde la reserva seleccionada,
        // no desde el ComboBox, para que funcione correctamente con la vista "TODAS LAS CANCHAS".
        btnEditarFecha.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona una reserva para editar.");
                return;
            }
            Reserva reservaSeleccionada = reservasMostradas.get(row);
            int idSel = reservaSeleccionada.getIdReserva();
            int canchaIdReal = reservaSeleccionada.getCanchaId();

            String nueva = JOptionPane.showInputDialog(this, "Nueva fecha (YYYY-MM-DD):", reservaSeleccionada.getFecha().toString());
            if (nueva == null || nueva.isBlank()) return;

            try {
                boolean ok = sistema.editarReservaFecha(canchaIdReal, idSel, LocalDate.parse(nueva.trim()));
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "No se pudo editar.");
                } else {
                    new GestionArchivos().guardarReservas(sistema);
                }
                cargarTabla();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fecha inválida.");
            }
        });

        btnEditarBloque.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona una reserva para editar.");
                return;
            }
            Reserva reserva = reservasMostradas.get(row);
            int idSel = reserva.getIdReserva();
            int canchaIdReal = reserva.getCanchaId();
            
            Cancha cancha = sistema.getCancha(canchaIdReal);
            if (cancha == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la cancha asociada a la reserva.");
                return;
            }

            List<BloqueHorario> bloquesDisponibles = new ArrayList<>();
            for (BloqueHorario bloque : BloqueHorario.values()) {
                if (cancha.estaDisponible(reserva.getFecha(), bloque) || bloque == reserva.getBloque()) {
                    bloquesDisponibles.add(bloque);
                }
            }
            if (bloquesDisponibles.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay horarios disponibles para esta fecha.");
                return;
            }

            JComboBox<BloqueHorario> cboBloques = new JComboBox<>(bloquesDisponibles.toArray(new BloqueHorario[0]));
            cboBloques.setRenderer(new DefaultListCellRenderer() {
                @Override
                public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof BloqueHorario) {
                        setText(((BloqueHorario) value).getDescripcion());
                    }
                    return this;
                }
            });
            cboBloques.setSelectedItem(reserva.getBloque());

            int result = JOptionPane.showConfirmDialog(this, cboBloques, "Seleccione el nuevo horario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                BloqueHorario nuevoBloque = (BloqueHorario) cboBloques.getSelectedItem();
                 if (!cancha.estaDisponible(reserva.getFecha(), nuevoBloque) && nuevoBloque != reserva.getBloque()) {
                    JOptionPane.showMessageDialog(this, "Este horario acaba de ser ocupado.", "Conflicto de Reserva", JOptionPane.WARNING_MESSAGE);
                    cargarTabla();
                    return;
                }
                boolean ok = sistema.editarReservaBloque(canchaIdReal, idSel, nuevoBloque);
                if (!ok) {
                    JOptionPane.showMessageDialog(this, "No se pudo editar la reserva.");
                } else {
                    new GestionArchivos().guardarReservas(sistema);
                    JOptionPane.showMessageDialog(this, "Horario actualizado correctamente.");
                }
                cargarTabla();
            }
        });

        btnEliminar.addActionListener(e -> {
            int row = tabla.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Selecciona una reserva para eliminar.");
                return;
            }
            Reserva reservaSeleccionada = reservasMostradas.get(row);
            int idSel = reservaSeleccionada.getIdReserva();
            int canchaIdReal = reservaSeleccionada.getCanchaId();

            int conf = JOptionPane.showConfirmDialog(this, "¿Eliminar la reserva #" + idSel + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;

            boolean ok = sistema.eliminarReserva(canchaIdReal, idSel);
            if (!ok) {
                JOptionPane.showMessageDialog(this, "No se encontró la reserva.");
            } else {
                new GestionArchivos().guardarReservas(sistema);
            }
            cargarTabla();
        });

        btnVolver.addActionListener(e -> frame.cambiarPanel(new PanelMenuPrincipal(frame, sistema)));

        if (cboCancha.getItemCount() > 0) cboCancha.setSelectedIndex(0);
        cargarTabla();
    }

    private int getCanchaSeleccionadaId() {
        Cancha c = (Cancha) cboCancha.getSelectedItem();
        return (c != null) ? c.getId() : -1;
    }

    private void cargarTabla() {
        model.setRowCount(0);
        int canchaIdSeleccionada = getCanchaSeleccionadaId();
        
        // -> CAMBIO: Decidir qué lista de reservas cargar.
        if (canchaIdSeleccionada == -1) { // Si se seleccionó "TODAS LAS CANCHAS"
            this.reservasMostradas = sistema.getTodasLasReservas();
            // Ordenar por fecha para que la vista general sea coherente.
            this.reservasMostradas.sort(Comparator.comparing(Reserva::getFecha));
        } else { // Si se seleccionó una cancha específica
            this.reservasMostradas = sistema.listarReservasOrdenadas(canchaIdSeleccionada);
        }
        
        for (Reserva r : this.reservasMostradas) {
        String nombreSocio = "Socio no encontrado";
        String telefonoSocio = "N/A";

        try {
            Socio socio = sistema.getSocioByRut(r.getRutSocio());
            // Si el método tiene éxito (no lanza excepción), actualizamos los datos
            nombreSocio = socio.getNombre();
            telefonoSocio = socio.getTelefono();
        } catch (SocioNoEncontradoException e) {
            // Si la excepción ocurre, no hacemos nada.
            // Las variables se quedan con sus valores por defecto ("Socio no encontrado").
        }
            Cancha canchaDeLaReserva = sistema.getCancha(r.getCanchaId());

            String nombreCancha = "Desconocida";
            if(canchaDeLaReserva != null){
                nombreCancha = canchaDeLaReserva.getNombre();
            }
            
            model.addRow(new Object[]{
                r.getIdReserva(),
                r.getRutSocio(),
                nombreSocio,
                telefonoSocio,
                r.getFecha(),
                r.getBloque().getDescripcion(),
                nombreCancha // -> Añadir el nombre de la cancha a la fila
            });
        }
    }
}
