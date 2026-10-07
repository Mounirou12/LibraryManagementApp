package com.projet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class BookDialog extends JDialog {

    private JTextField titleField;
    private JTextField authorField;
    private JComboBox<String> categoryBox;
    private JComboBox<BookStatus> statusBox;
    private boolean saved = false;

    public BookDialog(Window owner) {
        super(owner, "Ajouter un livre", Dialog.ModalityType.APPLICATION_MODAL);
        setSize(450, 420);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 30, 25, 30));

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

        // --- Champs ---
        titleField = new JTextField();
        authorField = new JTextField();
        categoryBox = new JComboBox<>(new String[]{
                "Programming", "Roman", "Science", "Histoire", "Art", "Autre"
        });
        statusBox = new JComboBox<>(new BookStatus[]{
                BookStatus.DISPONIBLE, BookStatus.EMPRUNTE, BookStatus.RESERVE
        });

        content.add(createField("Titre", titleField));
        content.add(Box.createVerticalStrut(12));
        content.add(createField("Auteur", authorField));
        content.add(Box.createVerticalStrut(12));
        content.add(createField("Catégorie", categoryBox));
        content.add(Box.createVerticalStrut(12));
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
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String category = (String) categoryBox.getSelectedItem();
        BookStatus status = (BookStatus) statusBox.getSelectedItem();

        if (title.isEmpty() || author.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le titre et l'auteur sont obligatoires.",
                    "Champs manquants",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (Books.insertBook(title, author, category, status)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Impossible d'ajouter le livre.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}