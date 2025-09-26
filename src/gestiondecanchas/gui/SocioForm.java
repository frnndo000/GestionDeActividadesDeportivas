package gestiondecanchas.gui;

import gestiondecanchas.Socio;
import javax.swing.*;
import java.awt.*;

public class SocioForm extends JDialog {
    private boolean ok = false;
    private final JTextField tfRut = new JTextField(12);
    private final JTextField tfNom = new JTextField(18);
    private final JTextField tfTel = new JTextField(12);

    /**
     * Constructor para editar un socio existente o crear uno nuevo sin datos.
     */
    public SocioForm(Window owner, Socio s) {
        super(owner, "Socio", ModalityType.APPLICATION_MODAL);
        setupUI();

        if (s != null) { // Modo Edición
            tfRut.setText(s.getRut());
            tfNom.setText(s.getNombre());
            tfTel.setText(s.getTelefono());
            tfRut.setEnabled(false); // El RUT no se puede editar
        }
    }
    
    /**
     * NUEVO CONSTRUCTOR: Para crear un socio nuevo pre-rellenando el RUT.
     */
    public SocioForm(Window owner, String rutInicial) {
        super(owner, "Nuevo Socio", ModalityType.APPLICATION_MODAL);
        setupUI();
        tfRut.setText(rutInicial); // Rellenamos el RUT que el usuario ya ingresó
    }

    private void setupUI() {
        setSize(380, 220);
        setLocationRelativeTo(getOwner());
        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; add(new JLabel("RUT:"), g);
        g.gridx = 1; g.gridy = 0; add(tfRut, g);
        g.gridx = 0; g.gridy = 1; add(new JLabel("Nombre:"), g);
        g.gridx = 1; g.gridy = 1; add(tfNom, g);
        g.gridx = 0; g.gridy = 2; add(new JLabel("Teléfono:"), g);
        g.gridx = 1; g.gridy = 2; add(tfTel, g);

        JButton btnOk = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnCancel);
        buttons.add(btnOk);

        g.gridx = 0; g.gridy = 3; g.gridwidth = 2; add(buttons, g);

        btnOk.addActionListener(e -> { ok = true; setVisible(false); });
        btnCancel.addActionListener(e -> { ok = false; setVisible(false); });
    }

    public boolean isOk() { return ok; }

    public Socio getSocio() {
        if (!ok) return null;
        return new Socio(tfRut.getText().trim(), tfNom.getText().trim(), tfTel.getText().trim());
    }
}