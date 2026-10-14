package com.projet;

import java.awt.*;
import java.time.LocalDate;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class MemberDialog extends JDialog {

    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private DatePickerField memberShipDateField;
    private JComboBox<MemberStatus> statusBox;
    private boolean saved = false;

    public MemberDialog(Window owner) {
        super(owner, "Ajouter un membre", Dialog.ModalityType.APPLICATION_MODAL);
        setSize(500, 500);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder((new EmptyBorder(25, 30, 25, 30)));

        // --- Titre de la fenêtre ---
        JLabel header = new JLabel("Nouveau livre");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(0x1F, 0x29, 0x37));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);

        JLabel sub = new JLabel("Remplissez les informations ci-dessous");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x6B, 0x72, 0x80));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sub);

        content.add(Box.createVerticalStrut(20));

        firstNameField = new JTextField();
        lastNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        memberShipDateField = new DatePickerField(LocalDate.now());
        statusBox = new JComboBox<>(new MemberStatus[] {
                MemberStatus.ACTIF, MemberStatus.SUSPENDU
        });

        content.add(createField("FirstName", firstNameField));
        content.add(createField("LastName", lastNameField));
        content.add(createField("Email", emailField));
        content.add(createField("Phone", phoneField));
        content.add(createField("MemberShipDate", memberShipDateField));
        content.add(createField("Statut", statusBox));

        content.add(Box.createVerticalStrut(25));

        // --- Boutons ---
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttons.setBackground(Color.WHITE);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBackground(Color.WHITE);
        cancelBtn.setForeground(new Color(0x37, 0x41, 0x51));
        cancelBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD1, 0xD5, 0xDB)),
                new EmptyBorder(6, 16, 6, 16)));
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.addActionListener(e -> dispose());

        JButton saveBtn = new JButton("Enregistrer");
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setFocusPainted(false);
        saveBtn.setBackground(new Color(0x3B, 0x82, 0xF6));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setBorder(new EmptyBorder(6, 16, 6, 16));
        saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        saveBtn.addActionListener(e -> onSave());

        buttons.add(cancelBtn);
        buttons.add(saveBtn);

        content.add(buttons);

        setContentPane(content);
        getRootPane().setDefaultButton(saveBtn);
    }

    private JPanel createField(String label, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(0x37, 0x41, 0x51));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(5));

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        field.setPreferredSize(new Dimension(0, 35));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        if (field instanceof JTextField) {
            ((JTextField) field).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xD1, 0xD5, 0xDB), 1, true),
                    new EmptyBorder(5, 10, 5, 10)));
        }

        panel.add(field);
        return panel;
    }

    private void onSave() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        LocalDate memberShipDate = memberShipDateField.getLocalDate();
        MemberStatus status = (MemberStatus) statusBox.getSelectedItem();

        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    " Le prenom, le nom, l'email et le numéro de telephone sont obligatoires.",
                    "Champs manquants",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Members.insertMember(firstName, lastName, email, phone, memberShipDate, status)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Impossible d'ajouter le membre.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
