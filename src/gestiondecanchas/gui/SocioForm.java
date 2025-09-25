/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author et
 */
package gestiondecanchas.gui;

import gestiondecanchas.Socio;
import javax.swing.*;
import java.awt.*;

public class SocioForm extends JDialog {
    private final JTextField tfRut = new JTextField(12);
    private final JTextField tfNom = new JTextField(20);
    private final JTextField tfTel = new JTextField(12);
    private boolean ok = false;

    public SocioForm(Window owner, Socio s) {
        super(owner, s==null?"Nuevo socio":"Editar socio", ModalityType.APPLICATION_MODAL);

        JPanel p = new JPanel(new GridLayout(0,2,8,8));
        p.add(new JLabel("RUT:")); p.add(tfRut);
        p.add(new JLabel("Nombre:")); p.add(tfNom);
        p.add(new JLabel("Teléfono:")); p.add(tfTel);

        JButton btnOk = new JButton("Guardar");
        JButton btnCancel = new JButton("Cancelar");
        btnOk.addActionListener(e -> { ok = true; dispose(); });
        btnCancel.addActionListener(e -> dispose());

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        south.add(btnCancel); south.add(btnOk);

        add(p, BorderLayout.CENTER); add(south, BorderLayout.SOUTH);
        pack(); setLocationRelativeTo(owner);

        if (s != null) {
            tfRut.setText(s.getRut());
            tfNom.setText(s.getNombre());
            tfTel.setText(s.getTelefono());
            tfRut.setEnabled(false); // RUT como PK
        }
    }

    public boolean isOk() { return ok; }
    public Socio getSocio() {
        return new Socio(tfRut.getText().trim(), tfNom.getText().trim(), tfTel.getText().trim());
    }
}
