package com.projet;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        searchRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0)); // petit espace sous le titre

        PlaceholderTextField search = new PlaceholderTextField("Search by title or author");
        search.setPreferredSize(new Dimension(250, 35));
        search.setForeground(TEXT_GRAY);
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        JButton addBtn = new JButton("+  Add Book");
        addBtn.setBackground(Color.blue);
        addBtn.setForeground(Color.white);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setRolloverEnabled(false);
        addBtn.setOpaque(true);
        addBtn.setContentAreaFilled(true);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setPreferredSize(new Dimension(200, 35));
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Le champ de recherche occupe le centre-gauche, le bouton va à l'extrême
        // droite
        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.add(search);

        searchRow.add(searchWrapper, BorderLayout.WEST);
        searchRow.add(addBtn, BorderLayout.EAST);

        // --- Assemblage final dans le header ---
        header.add(title, BorderLayout.NORTH);
        header.add(searchRow, BorderLayout.CENTER);

        return header;
    }

    private JScrollPane createTable() {
        // Données d'exemple (à remplacer par CrudBooks.getAllBooks())
        String[] columns = { "Title", "Author", "Category", "Status", "Actions" };
        Object[][] data = {
                { "Clean Code", "Robert C. Martin", "Programming", "Available", "" },
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // seule la colonne Actions est éditable
            }
        };
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setForeground(TEXT_DARK);
        table.setGridColor(BORDER_GRAY);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(0xEF, 0xF6, 0xFF));
        table.setSelectionForeground(TEXT_DARK);

        // Style de l'en-tête
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0xF9, 0xFA, 0xFB));
        header.setForeground(TEXT_GRAY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_GRAY));
        header.setPreferredSize(new Dimension(0, 40));

        // Renderer pour la colonne Status
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        // Renderer/Editor pour la colonne Actions
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ActionEditor(table));

        // Largeurs
        table.getColumnModel().getColumn(0).setPreferredWidth(180);
        table.getColumnModel().getColumn(1).setPreferredWidth(160);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
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

            if ("Available".equals(value)) {
                label.setBackground(new Color(0xD1, 0xFA, 0xE5));
                label.setForeground(GREEN);
            } else {
                label.setBackground(new Color(0xFE, 0xF3, 0xC7));
                label.setForeground(ORANGE);
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

    static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setBackground(Color.WHITE);

            JButton edit = new JButton("✏️");
            JButton delete = new JButton("🗑️");
            for (JButton b : new JButton[] { edit, delete }) {
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            panel.add(edit);
            panel.add(delete);
            return panel;
        }
    }

    static class ActionEditor extends DefaultCellEditor {
        private final JPanel panel;
        private final JButton edit;
        private final JButton delete;
        private int currentRow;

        public ActionEditor(JTable table) {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setBackground(Color.WHITE);

            edit = new JButton("✏️");
            delete = new JButton("🗑️");

            for (JButton b : new JButton[] { edit, delete }) {
                b.setBorderPainted(false);
                b.setContentAreaFilled(false);
                b.setFocusPainted(false);
                b.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            edit.addActionListener(e -> {
                fireEditingStopped();
                JOptionPane.showMessageDialog(null,
                        "Modifier la ligne " + currentRow);
                // Ici : ouvrir une fenêtre d'édition
            });

            delete.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null,
                        "Supprimer cette ligne ?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    ((DefaultTableModel) table.getModel()).removeRow(currentRow);
                    // Ici : appeler CrudBooks.deleteBook(id)
                }
            });
            panel.add(edit);
            panel.add(delete);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
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
