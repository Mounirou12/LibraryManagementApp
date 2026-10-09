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
import java.util.ArrayList;
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
    private final List<JButton> menuButtons = new ArrayList<>();
    private JButton activeButton;
    private JPanel contentArea; // la zone qui change
    private CardLayout cardLayout;

    public LibrairiePage() {
        setTitle("Library Manager-Java");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(Color.WHITE);

        contentArea.add(createDashboardPage(), "dashboard");
        contentArea.add(createBooksPage(), "books");
        contentArea.add(createTable(), "members");
        contentArea.add(createTable(), "borrowing");
        add(createSidebar(), BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);

        // ⚠️ 4. Page par défaut
        showPage("dashboard");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBackground(Color.white);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel logo = new JLabel("📚 Library");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        logo.setForeground(TEXT_DARK);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));

        sidebar.add(createMenuItem("🏠  Dashboard", "dashboard", true));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("📖  Books", "books", false));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("👥  Members", "members", false));
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(createMenuItem("🔖  Borrowing", "borrowing", false));
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private JButton createMenuItem(String text, String pageKey, boolean active) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_DARK);
        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setRolloverEnabled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        menuButtons.add(btn);
        if (active)
            activeButton = btn;

        btn.addActionListener(e -> {
            setActiveButton(btn);
            showPage(pageKey);
        });

        return btn;
    }

    private void setActiveButton(JButton button) {
        if (button == activeButton)
            return;

        if (activeButton != null) {
            activeButton.setBackground(Color.WHITE);
            activeButton.setForeground(TEXT_GRAY);
        }

        button.setBackground(SIDEBAR_ACTIVE);
        button.setForeground(TEXT_DARK);

        activeButton = button;
    }

    private void showPage(String pageKey) {
        if (contentArea == null)
            return;
        cardLayout.show(contentArea, pageKey);
    }

    private PlaceholderTextField search; // ⚠️ champ de classe
    private PlaceholderTextField dashboardSearch;
    private PlaceholderTextField booksSearch;
    private DefaultTableModel model; // ⚠️ champ de classe
    private DefaultTableModel dashboardModel;
    private DefaultTableModel booksModel; // ⚠️ champ de classe
    private Timer booksDebounceTimer;
    private Timer dashboardDebounceTimer;

    private JTable table;

    private void loadBooksIntoModel(List<Book> books) {
        booksModel.setRowCount(0); // ⚠️ booksModel (camelCase)
        for (Book b : books) {
            booksModel.addRow(new Object[] {
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getStatus(),
                    "", // Edit
                    "" // Delete
            });
        }
    }

    private void loadDashboardData(List<Book> books) {
        dashboardModel.setRowCount(0);
        for (Book b : books) {
            dashboardModel.addRow(new Object[] {
                    b.getId(),
                    b.getTitle(),
                    b.getAuthor(),
                    b.getCategory(),
                    b.getStatus(),
            });
        }
    }

    private void onEditBook(JTable table, int row) {
        int bookId = asInt(table.getModel().getValueAt(row, 0));
        String title = table.getModel().getValueAt(row, 1).toString();
        String author = table.getModel().getValueAt(row, 2).toString();
        String category = table.getModel().getValueAt(row, 3).toString();

        BookStatus status = BookStatus.valueOf(
                table.getModel().getValueAt(row, 4).toString());

        Book book = new Book(bookId, title, author, category, status);
        BookUpdateDialog dialog = new BookUpdateDialog(
                SwingUtilities.getWindowAncestor(this), book);
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            loadBooksIntoModel(Books.getAllMembers());
            loadDashboardData(Books.getAllMembers());
        }

    }

    private void onDeleteBook(JTable table, int row) {
        int bookId = asInt(table.getModel().getValueAt(row, 0)); // ✅ colonne 0
        String title = table.getModel().getValueAt(row, 1).toString(); // ✅ colonne 1

        int confirm = JOptionPane.showConfirmDialog(this,
                "Supprimer le livre \"" + title + "\" ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION && Books.deleteBook(bookId)) {
            loadBooksIntoModel(Books.getAllMembers());
            loadDashboardData(Books.getAllMembers());
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

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // --- Ligne du haut : le titre ---
        JLabel title = new JLabel("Library Books");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(TEXT_DARK);

        // --- Ligne du bas : recherche (gauche) + bouton (droite) ---

        header.add(title, BorderLayout.WEST);

        return header;
    }

    private JPanel createToolbarDashboard() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        dashboardSearch = new PlaceholderTextField("Search by title or author"); // ⚠️ champ de classe
        dashboardSearch.setPreferredSize(new Dimension(250, 35));
        dashboardSearch.setForeground(TEXT_DARK); // ⚠️ TEXT_GRAY était pour le placeholder
        dashboardSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        // ⚠️ Debounce pour éviter une requête SQL à chaque frappe
        dashboardDebounceTimer = new Timer(300, e -> {
            String texte = dashboardSearch.getText().trim();
            List<Book> resultats = texte.isEmpty()
                    ? Books.getAllMembers()
                    : Books.searchBooks(texte);
            loadDashboardData(resultats); // ⚠️ met à jour le tableau
        });
        dashboardDebounceTimer.setRepeats(false);

        dashboardSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void relancer() {
                if (dashboardDebounceTimer.isRunning())
                    dashboardDebounceTimer.stop();
                dashboardDebounceTimer.start();
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

        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.add(dashboardSearch);

        toolbar.add(searchWrapper, BorderLayout.CENTER);
        return toolbar;

    }

    private JPanel createToolbar() {
        JPanel toolbar = new JPanel(new BorderLayout(10, 0));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JPanel searchRow = new JPanel(new BorderLayout(10, 0));
        searchRow.setBackground(Color.WHITE);
        searchRow.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // --- Champ de recherche (gauche) ---
        booksSearch = new PlaceholderTextField("Search by title or author");
        booksSearch.setPreferredSize(new Dimension(250, 35));
        booksSearch.setForeground(TEXT_DARK);
        booksSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_GRAY, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        booksDebounceTimer = new Timer(300, e -> {
            String texte = booksSearch.getText().trim();
            List<Book> resultats = texte.isEmpty()
                    ? Books.getAllMembers()
                    : Books.searchBooks(texte);
            loadBooksIntoModel(resultats);
        });
        booksDebounceTimer.setRepeats(false);

        booksSearch.getDocument().addDocumentListener(new DocumentListener() {
            private void relancer() {
                if (booksDebounceTimer.isRunning())
                    booksDebounceTimer.stop();
                booksDebounceTimer.start();
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
        JButton addBtn = new JButton("+  Add Book");
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
            BookDialog dialog = new BookDialog(
                    SwingUtilities.getWindowAncestor(this));
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadBooksIntoModel(Books.getAllMembers());
            }
        });

        // --- Assemblage ---
        JPanel searchWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchWrapper.setBackground(Color.WHITE);
        searchWrapper.add(booksSearch);

        toolbar.add(searchWrapper, BorderLayout.WEST);
        toolbar.add(addBtn, BorderLayout.EAST);
        return toolbar;
    }

    private JPanel createDashboardPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane tableScroll = tableDashboard();

        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.WHITE);
        top.add(createHeader());
        top.add(createToolbarDashboard(), BorderLayout.CENTER);

        page.add(top, BorderLayout.NORTH);
        page.add(tableScroll, BorderLayout.CENTER);

        return page;
    }

    private JPanel createBooksPage() {
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(Color.WHITE);
        page.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // ⚠️ 1. Créer la table EN PREMIER (initialise booksModel)
        JScrollPane tableScroll = createTable();

        // ⚠️ 2. Ensuite créer la toolbar (utilise booksModel)
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.WHITE);
        top.add(createHeader());
        top.add(createToolbar());

        page.add(top, BorderLayout.NORTH);
        page.add(tableScroll, BorderLayout.CENTER);

        return page;
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

    private JScrollPane tableDashboard() {
        String[] columns = { "ID", "Title", "Author", "Category", "Status" };

        // Modèle vide au départ
        dashboardModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Chargement des données depuis la base
        loadDashboardData(Books.getAllMembers());

        JTable table = new JTable(dashboardModel);
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

        // Renderers
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());

        // Largeurs
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    private JScrollPane createTable() {
        String[] columns = { "ID", "Title", "Author", "Category", "Status", "Edit", "Delete" };

        // Modèle vide au départ
        booksModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5 || column == 6; // seule la colonne Actions est éditable
            }
        };

        // Chargement des données depuis la base
        loadBooksIntoModel(Books.getAllMembers());

        JTable table = new JTable(booksModel);
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

        // Renderers
        table.getColumnModel().getColumn(4).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new EditRenderer());
        table.getColumnModel().getColumn(5).setCellEditor(new EditEditor(
                row -> onEditBook(table, row)));
        table.getColumnModel().getColumn(6).setCellRenderer(new DeleteRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new DeleteEditor(
                row -> onDeleteBook(table, row)));

        // Largeurs
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(160);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_GRAY));
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // ⚠️ Normaliser en BookStatus
            BookStatus status;
            if (value instanceof BookStatus) {
                status = (BookStatus) value;
            } else if (value != null) {
                status = BookStatus.fromString(value.toString());
            } else {
                status = BookStatus.DISPONIBLE;
            }

            // Créer le label
            JLabel label = new JLabel(status.getLabel());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

            // Couleurs selon le statut
            Color bg, fg;
            switch (status) {
                case DISPONIBLE -> {
                    bg = new Color(0xD1, 0xFA, 0xE5);
                    fg = new Color(0x06, 0x5F, 0x46);
                }
                case RESERVE -> {
                    bg = new Color(0xFE, 0xF3, 0xC7);
                    fg = new Color(0x92, 0x40, 0x0E);
                }
                case EMPRUNTE -> {
                    bg = new Color(0xFE, 0xE2, 0xE2);
                    fg = new Color(0x99, 0x1B, 0x1B);
                }
                default -> {
                    bg = Color.WHITE;
                    fg = Color.BLACK;
                }
            }

            // Sélection : assombrir légèrement
            if (isSelected) {
                bg = bg.darker();
            }

            label.setBackground(bg);
            label.setForeground(fg);

            // ⚠️ Pas de wrap JPanel : on retourne le label directement
            // pour que la sélection fonctionne correctement
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
