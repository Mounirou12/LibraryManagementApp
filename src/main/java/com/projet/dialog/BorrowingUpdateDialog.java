package com.projet.dialog;

import com.projet.page.design.DatePickerField;
import com.projet.controller.Books;
import com.projet.controller.Borrowings;
import com.projet.controller.Members;
import com.projet.model.Book;
import com.projet.model.Borrowing;
import com.projet.model.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;

public class BorrowingUpdateDialog extends JDialog {

    private JComboBox<Book> bookBox;
    private JComboBox<Member> memberBox;
    private DatePickerField borrowDateField;
    private DatePickerField dueDateField;
    private JCheckBox returnedCheck;
    private DatePickerField returnDateField;

    private boolean saved = false;
    private final Borrowing borrowing;

    public BorrowingUpdateDialog(Window owner, Borrowing borrowing) {
        super(owner, "Modifier l'emprunt", Dialog.ModalityType.APPLICATION_MODAL);
        this.borrowing = borrowing;

        setSize(500, 580);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(25, 30, 25, 30));

        // --- Header ---
        JLabel header = new JLabel("Modifier l'emprunt");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(0x1F, 0x29, 0x37));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);

        JLabel sub = new JLabel("Modifiez les informations puis enregistrez");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0x6B, 0x72, 0x80));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(sub);

        content.add(Box.createVerticalStrut(20));

        // --- Champs ---
        bookBox = new JComboBox<>();
        memberBox = new JComboBox<>();

        // Charger tous les livres (y compris celui déjà emprunté)
        for (Book b : Books.getAllBooks()) {
            bookBox.addItem(b);
        }

        // Charger tous les membres
        for (Member m : Members.getAllMembers()) {
            memberBox.addItem(m);
        }

        // Renderers
        bookBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Book b) setText(b.getTitle());
                return this;
            }
        });

        memberBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Member m) setText(m.getFirstName() + " " + m.getLastName());
                return this;
            }
        });

        // ⚠️ Pré-sélectionner le livre et le membre actuels
        for (int i = 0; i < bookBox.getItemCount(); i++) {
            if (bookBox.getItemAt(i).getId() == borrowing.getBookId()) {
                bookBox.setSelectedIndex(i);
                break;
            }
        }
        for (int i = 0; i < memberBox.getItemCount(); i++) {
            if (memberBox.getItemAt(i).getId() == borrowing.getMemberId()) {
                memberBox.setSelectedIndex(i);
                break;
            }
        }

        // Dates
        borrowDateField = new DatePickerField(borrowing.getBorrowDate());
        dueDateField = new DatePickerField(borrowing.getDuDateDtDate());

        // Case "Rendu"
        returnedCheck = new JCheckBox("Rendu");
        returnedCheck.setBackground(Color.WHITE);
        returnedCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        returnedCheck.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Date de retour
        returnDateField = new DatePickerField(
                borrowing.getReturnDate() != null
                        ? borrowing.getReturnDate()
                        : LocalDate.now());

        // ⚠️ Cocher si déjà retourné
        boolean isReturned = borrowing.getReturnDate() != null;
        returnedCheck.setSelected(isReturned);
        returnDateField.setEnabled(isReturned);

        returnedCheck.addActionListener(e -> {
            returnDateField.setEnabled(returnedCheck.isSelected());
        });

        content.add(createField("Livre", bookBox));
        content.add(createField("Membre", memberBox));
        content.add(createField("Date d'emprunt", borrowDateField));
        content.add(createField("Date d'échéance", dueDateField));
        content.add(createField("", returnedCheck));
        content.add(createField("Date de retour", returnDateField));

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

        JButton saveBtn = new JButton("Mettre à jour");
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

        if (field instanceof JTextField tf) {
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xD1, 0xD5, 0xDB), 1, true),
                    new EmptyBorder(5, 10, 5, 10)));
        }

        panel.add(field);
        return panel;
    }

    private void onSave() {
        Book selectedBook = (Book) bookBox.getSelectedItem();
        Member selectedMember = (Member) memberBox.getSelectedItem();

        if (selectedBook == null || selectedMember == null) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un livre et un membre.",
                    "Champs manquants",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        LocalDate borrowDate = borrowDateField.getLocalDate();
        LocalDate dueDate = dueDateField.getLocalDate();
        LocalDate returnDate = returnedCheck.isSelected()
                ? returnDateField.getLocalDate()
                : null;

        // Validations
        if (dueDate.isBefore(borrowDate)) {
            JOptionPane.showMessageDialog(this,
                    "La date d'échéance doit être après la date d'emprunt.",
                    "Dates invalides",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (returnDate != null && returnDate.isBefore(borrowDate)) {
            JOptionPane.showMessageDialog(this,
                    "La date de retour doit être après la date d'emprunt.",
                    "Dates invalides",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mise à jour
        if (Borrowings.updateBorrowing(
                borrowing.getId(),
                selectedBook.getId(),
                selectedMember.getId(),
                borrowDate, dueDate, returnDate)) {
            saved = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Impossible de mettre à jour l'emprunt.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}