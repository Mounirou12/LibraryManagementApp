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
import java.util.List;
import java.util.function.IntConsumer;

public class LibrairiePage extends JFrame {
    private static final Color SIDEBAR_BG = new Color(0xE8, 0xF1, 0xFF);
    private static final Color SIDEBAR_ACTIVE = new Color(0x3B, 0x82, 0xF6); // bleu actif
    private static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private static final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_GRAY = new Color(0xE5, 0xE7, 0xEB);
    private static final Color GREEN = new Color(0x10, 0xB9, 0x81);
    private static final Color ORANGE = new Color(0xF5, 0x9E, 0x0B);

    public LibrairiePage() {
        setTitle("Library Manager-Java");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        add(createSidebar(), BorderLayout.WEST);
        add(createMainPanel(), BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel logo = new JLabel("📚 Library");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(TEXT_DARK);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createMenuItem("🏠  Dashboard", true));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("📖  Books", false)); // actif
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("👥  Members", false));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("🔖  Borrowing", false));

        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createMenuItem(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (active) {
            btn.setBackground(SIDEBAR_ACTIVE);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(SIDEBAR_BG);
            btn.setForeground(TEXT_DARK);
        }
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!active)
                    btn.setBackground(new Color(0xD6, 0xE4, 0xFA));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!active)
                    btn.setBackground(SIDEBAR_BG);
            }
        });
        return btn;
    }

    private JPanel createMainPanel() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Color.WHITE);
        main.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createTable(), BorderLayout.CENTER);
        return main;
    }

    private PlaceholderTextField search; // ⚠️ champ de classe
    private DefaultTableModel model; // ⚠️ champ de classe
    private JTable table;

    private void loadBooksIntoModel(List<Book> books) {
        model.setRowCount(0);
        for (Book b : books) {
            model.addRow(new Object[] {
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getStatus(),
                    ""
            });
        }
    }

    private void onEditBook(JTable table, int row) {
        int bookId = (int) table.getModel().getValueAt(row, 0);
        String title = (String) table.getModel().getValueAt(row, 1);
        String author = (String) table.getModel().getValueAt(row, 2);
        System.out.println("Modifier : ID=" + bookId + ", titre=" + title);
    }

    private void onDeleteBook(JTable table, int row) {
        int bookId = (int) table.getModel().getValueAt(row, 0);
        String title = (String) table.getModel().getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Supprimer le livre \"" + title + "\" ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (Books.deleteBook(bookId)) {
                loadBooksIntoModel(Books.getAllMembers()); // recharge
            } else {
                JOptionPane.showMessageDialog(this,
                        "Impossible de supprimer ce livre.",
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // --- Ligne du haut : le titre ---
        JLabel title = new JLabel("Library Books");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        // --- Ligne du bas : recherche (gauche) + bouton (droite) ---
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        search = new PlaceholderTextField("Search by title or author"); // ⚠️ champ de classe
        search.setPreferredSize(new Dimension(250, 35));
        search.setForeground(TEXT_DARK); // ⚠️ TEXT_GRAY était pour le placeholder
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // ⚠️ Debounce pour éviter une requête SQL à chaque frappe
        Timer debounceTimer = new Timer(300, e -> {
            String texte = search.getText().trim();
            List<Book> resultats = texte.isEmpty()
                    ? Books.getAllMembers()
                    : Books.searchBooks(texte);
            loadBooksIntoModel(resultats); // ⚠️ met à jour le tableau
        });
        debounceTimer.setRepeats(false);

        search.getDocument().addDocumentListener(new DocumentListener() {
            private void relancer() {
                if (debounceTimer.isRunning())
                    debounceTimer.stop();
                debounceTimer.start();
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

        JButton addBtn = new JButton("+  Add Book");
        addBtn.setBackground(Color.BLUE);
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setRolloverEnabled(false);
        addBtn.setOpaque(true);
        addBtn.setContentAreaFilled(true);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setPreferredSize(new Dimension(200, 35));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.add(search);

        searchRow.add(searchWrapper, BorderLayout.WEST);
        searchRow.add(addBtn, BorderLayout.EAST);

        header.add(title, BorderLayout.NORTH);
        header.add(searchRow, BorderLayout.CENTER);

        return header;
    }

    private JScrollPane createTable() {
        String[] columns = { "Title", "Author", "Category", "Status", "Actions" };

        // Modèle vide au départ
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // seule la colonne Actions est éditable
            }
        };

        // Chargement des données depuis la base
        loadBooksIntoModel(Books.getAllMembers());

        JTable table = new JTable(model);
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

        ActionCell actionCell = new ActionCell(row -> onEditBook(table, row), row -> onDeleteBook(table, row));

        // Renderers
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(actionCell);
        table.getColumnModel().getColumn(4).setCellEditor(actionCell);

        // Largeurs
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));

            if ("DISPONIBLE".equals(value)) {
                label.setBackground(new Color(0xD1, 0xFA, 0xE5)); // #d8d1fa
                label.setForeground(new Color(0x06, 0x5F, 0x46)); // #065F46 (émeraude)
            } else if ("RESERVE".equals(value)) {
                label.setBackground(new Color(0xFE, 0xF3, 0xC7)); // #FEF3C7
                label.setForeground(new Color(0x92, 0x40, 0x0E)); // #92400E (cuivre)
            } else if ("EMPRUNTE".equals(value)) {
                label.setBackground(new Color(0xFE, 0xE2, 0xE2)); // #FEE2E2 rouge rosé très clair
                label.setForeground(new Color(0x99, 0x1B, 0x1B)); // #991B1B rouge bordeaux foncé
            }

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            // Encapsuler dans un JPanel pour ajouter une marge
            JPanel wrap = new JPanel(new BorderLayout());
            wrap.setBackground(Color.WHITE);
            wrap.setBorder(BorderFactory.createEmptyBorder(6, 20, 6, 20));
            wrap.add(label, BorderLayout.CENTER);
            return wrap;
        }
    }

   
     static class PlaceholderTextField extends JTextField {

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

    static class ActionCell extends AbstractCellEditor
            implements TableCellEditor, TableCellRenderer {

        private final JPanel panel;
        private final JButton editBtn;
        private final JButton deleteBtn;
        private int currentRow;
        private final IntConsumer onEdit;
        private final IntConsumer onDelete;

        public ActionCell(IntConsumer onEdit, IntConsumer onDelete) {
            this.onEdit = onEdit;
            this.onDelete = onDelete;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);

            editBtn = createIconButton("E", new Color(0x3B, 0x82, 0xF6));
            deleteBtn = createIconButton("X", new Color(0xEF, 0x44, 0x44));

            editBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    fireEditingStopped();
                    if (onEdit != null)
                        onEdit.accept(currentRow);
                }
            });

            deleteBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    fireEditingStopped();
                    if (onDelete != null)
                        onDelete.accept(currentRow);
                }
            });

            panel.add(editBtn);
            panel.add(deleteBtn);
        }

        private JButton createIconButton(String icon, Color color) {
            JButton btn = new JButton(icon);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btn.setForeground(color);
            btn.setBackground(Color.WHITE);
            btn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setRolloverEnabled(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }

        // ---------- Renderer ----------
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }

        // ---------- Editor ----------
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // ---------- MAIN ----------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new LibrairiePage().setVisible(true);
        });
    }
}
