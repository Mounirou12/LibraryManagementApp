package com.projet;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.function.IntConsumer;

public class CreateBorrowingsPage  extends JPanel{
    private PlaceholderTextField borrowingSearch;
    private DefaultTableModel borrowingModel;
    private Timer borrowingsDebounceTimer;
    private static final Color SIDEBAR_ACTIVE = new Color(0x3B, 0x82, 0xF6); // bleu actif
    private static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private static final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_GRAY = new Color(0xE5, 0xE7, 0xEB);

    public CreateBorrowingsPage(){
     setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane tableScroll = createTable();
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.white);
        top.add(createHeader());
        top.add(createToolbar());

        add(top, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);
    }

    private void loadBorrowingsIntoModel(List<BorrowingView> borrowings) {
        borrowingModel.setRowCount(0); // ⚠️ booksModel (camelCase)
        for (BorrowingView b : borrowings) {
            borrowingModel.addRow(new Object[] {
                    b.getId(),
                    b.getBookId(),
                    b.getMemberId(),
                    b.getBookTitle(),
                    b.getMemberFullName(),
                    b.getBorrowDate(),
                    b.getDueDate(),
                    b.getReturnDate() != null ? b.getReturnDate() : "",
                    b.getStatus(),
                    "",
                    ""
            });
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // --- Ligne du haut : le titre ---
        JLabel title = new JLabel("Library Borrowings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        // --- Ligne du bas : recherche (gauche) + bouton (droite) ---

        header.add(title, BorderLayout.WEST);

        return header;
    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // --- Champ de recherche (gauche) ---
        borrowingSearch = new PlaceholderTextField("Search by firstName or LastName");
        borrowingSearch.setPreferredSize(new Dimension(250, 35));
        borrowingSearch.setForeground(TEXT_DARK);
        borrowingSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        borrowingsDebounceTimer = new Timer(300, e -> {
            String texte = borrowingSearch.getText().trim();
            List<BorrowingView> resultats;

            if (texte.isEmpty()) {
                resultats = Borrowings.getAllBorrowingsView();
            } else {
                // Convertir la saisie en BorrowingStatus
                BorrowingStatus status = BorrowingStatus.fromString(texte);
                resultats = Borrowings.searchBorrowingsByStatus(status);
            }

            loadBorrowingsIntoModel(resultats);
        });
        borrowingsDebounceTimer.setRepeats(false);

        borrowingSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void relancer() {
                if (borrowingsDebounceTimer.isRunning())
                    borrowingsDebounceTimer.stop();
                borrowingsDebounceTimer.start();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                relancer();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                relancer();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                relancer();
            }
        });

        // --- Bouton Add (droite) ---
        JButton addBtn = new JButton("+  Add Borrowing");
        addBtn.setBackground(new Color(0x3B, 0x82, 0xF6));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setRolloverEnabled(false);
        addBtn.setOpaque(true);
        addBtn.setContentAreaFilled(true);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setPreferredSize(new Dimension(150, 35));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        addBtn.addActionListener(e -> {
            BorrowingDialog dialog = new BorrowingDialog(
                    SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadBorrowingsIntoModel(Borrowings.getAllBorrowingsView());
            }
        });

        // --- Assemblage ---
        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.add(borrowingSearch);

        toolbar.add(searchWrapper, BorderLayout.WEST);
        toolbar.add(addBtn, BorderLayout.EAST);
        return toolbar;
    }


    private JScrollPane createTable() {
        String[] columns = { "ID", "BookId","MemberId","Book", "Member", "Borrow Date",
                         "Due Date", "Return Date", "Status", "Edit", "Delete" };

        borrowingModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7 || column == 8;
            }
        };

        loadBorrowingsIntoModel(Borrowings.getAllBorrowingsView());

        JTable table = new JTable(borrowingModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER_GRAY);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(0xEF, 0xF6, 0xFF));
        table.setSelectionForeground(TEXT_DARK);

        // En-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setForeground(TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));
        header.setPreferredSize(new Dimension(0, 40));

        // ⚠️ Masquer la colonne ID
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        table.getColumnModel().getColumn(1).setMinWidth(0);
        table.getColumnModel().getColumn(1).setMaxWidth(0);
        table.getColumnModel().getColumn(1).setWidth(0);

        table.getColumnModel().getColumn(2).setMinWidth(0);
        table.getColumnModel().getColumn(2).setMaxWidth(0);
        table.getColumnModel().getColumn(2).setWidth(0);

        // Renderers
        table.getColumnModel().getColumn(8).setCellRenderer(new BorrowingStatusRenderer());
        table.getColumnModel().getColumn(9).setCellRenderer(new EditRenderer());
        table.getColumnModel().getColumn(9).setCellEditor(new EditEditor(row -> onEditBorrow(table, row)));
        table.getColumnModel().getColumn(10).setCellRenderer(new DeleteRenderer());
        table.getColumnModel().getColumn(10).setCellEditor(new DeleteEditor(row -> onDeleteBorrowing(table, row)));

        // Largeurs
        table.getColumnModel().getColumn(1).setPreferredWidth(180);  // Book
        table.getColumnModel().getColumn(2).setPreferredWidth(180);  // Member
        table.getColumnModel().getColumn(3).setPreferredWidth(110);  // Borrow Date
        table.getColumnModel().getColumn(4).setPreferredWidth(110);  // Due Date
        table.getColumnModel().getColumn(5).setPreferredWidth(110);  // Return Date
        table.getColumnModel().getColumn(6).setPreferredWidth(100);  // Status
        table.getColumnModel().getColumn(7).setPreferredWidth(70);   // Edit
        table.getColumnModel().getColumn(8).setPreferredWidth(70);   // Delete

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    static class EditRenderer extends JButton implements TableCellRenderer {

        public EditRenderer() {
            setText("Edit");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            setForeground(new Color(0x3B, 0x82, 0xF6));
            setBackground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setRolloverEnabled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }

    }

    static class EditEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private int currentRow;
        private final IntConsumer onEdit;

        public EditEditor(IntConsumer onEdit) {
            this.onEdit = onEdit;
            button = new JButton("Edit");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            button.setForeground(new Color(0x3B, 0x82, 0xF6));
            button.setBackground(Color.WHITE);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setRolloverEnabled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                    int rowToEdit = currentRow;
                    fireEditingStopped();
                    if (onEdit != null)
                        onEdit.accept(rowToEdit);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            button.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    static class DeleteRenderer extends JButton implements TableCellRenderer {
        public DeleteRenderer() {
            setText("Delete");
            setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            setForeground(new Color(0xEF, 0x44, 0x44));
            setBackground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setRolloverEnabled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    static class DeleteEditor extends AbstractCellEditor implements TableCellEditor {
        private final JButton button;
        private int currentRow;
        private final IntConsumer onDelete;

        public DeleteEditor(IntConsumer onDelete) {
            this.onDelete = onDelete;
            button = new JButton("Delete");
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            button.setForeground(Color.BLACK);
            button.setBackground(Color.white);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setRolloverEnabled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    e.consume();
                    int rowToDelete = currentRow;
                    fireEditingStopped();
                    if (onDelete != null)
                        onDelete.accept(rowToDelete);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            button.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    private static int asInt(Object value) {
        if (value == null)
            return -1;
        if (value instanceof Integer integer)
            return integer;
        try {
            return Integer.parseInt(value.toString().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void onEditBorrow(JTable table, int row) {
        int borrowingId = asInt(table.getModel().getValueAt(row, 0));
        int bookId = asInt(table.getModel().getValueAt(row, 1));
        int memberId = asInt(table.getModel().getValueAt(row, 2));
        LocalDate borrowDate = (LocalDate) table.getModel().getValueAt(row, 5);
        LocalDate dueDate = (LocalDate) table.getModel().getValueAt(row, 6);
        Object returnDateObj = table.getModel().getValueAt(row, 7);
        LocalDate returnDate = (returnDateObj instanceof LocalDate)
                ? (LocalDate) returnDateObj
                : null;
        BorrowingStatus status = BorrowingStatus.fromString(
                table.getModel().getValueAt(row, 8).toString());

        Borrowing borrowing = new Borrowing(
                borrowingId, bookId, memberId,
                borrowDate, dueDate, returnDate, status);

        BorrowingUpdateDialog dialog = new BorrowingUpdateDialog(
                SwingUtilities.getWindowAncestor(this), borrowing);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadBorrowingsIntoModel(Borrowings.getAllBorrowingsView());
        }
    }
    private void onDeleteBorrowing(JTable table, int row) {
        int memberId = asInt(table.getModel().getValueAt(row, 0));
        String firstName = table.getModel().getValueAt(row, 1).toString();
        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le mebre \"" + firstName + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && Members.deleteMember(memberId)) {
            loadBorrowingsIntoModel(Borrowings.getAllBorrowingsView());
        }
    }

    static class BorrowingStatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            // ⚠️ Normaliser en BorrowingStatus
            BorrowingStatus status;
            if (value instanceof BorrowingStatus) {
                status = (BorrowingStatus) value;
            } else if (value != null) {
                status = BorrowingStatus.fromString(value.toString());
            } else {
                status = BorrowingStatus.EN_COURS;
            }

            // Créer le label
            JLabel label = new JLabel(status.getLabel());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

            // Couleurs selon le statut d'emprunt
            Color bg, fg;
            switch (status) {
                case EN_COURS -> {
                    bg = new Color(0xDB, 0xEA, 0xFE);   // bleu clair
                    fg = new Color(0x1E, 0x40, 0xAF);   // bleu foncé
                }
                case RETOURNE -> {
                    bg = new Color(0xD1, 0xFA, 0xE5);   // vert menthe
                    fg = new Color(0x06, 0x5F, 0x46);   // vert forêt
                }
                case EN_RETARD -> {
                    bg = new Color(0xFE, 0xE2, 0xE2);   // rosé clair
                    fg = new Color(0x99, 0x1B, 0x1B);   // bordeaux
                }
                default -> {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                }
            }

            if (isSelected) bg = bg.darker();

            label.setBackground(bg);
            label.setForeground(fg);
            return label;
        }
    }
    public class PlaceholderTextField extends JTextField {

        private String placeholder;

        public PlaceholderTextField() {
            super();
        }

        public PlaceholderTextField(String placeholder) {
            super();
            this.placeholder = placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
        }

        public String getPlaceholder() {
            return placeholder;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (placeholder == null || placeholder.isEmpty() || !getText().isEmpty()) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create(); // ⚠️ suppression du cast inutile
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(getDisabledTextColor());
            g2.setFont(getFont());

            Insets insets = getInsets();
            FontMetrics fm = g2.getFontMetrics();
            int y = insets.top + fm.getAscent()
                    + (getHeight() - insets.top - insets.bottom - fm.getHeight()) / 2;

            g2.drawString(placeholder, insets.left, y);
            g2.dispose();
        }
    }

}
