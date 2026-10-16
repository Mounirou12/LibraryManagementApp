package com.projet.page.view;

import com.projet.controller.Books;
import com.projet.model.Book;
import com.projet.status.BookStatus;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.util.List;

public class CreateDashboardPage  extends JPanel{

    private static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private static final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_GRAY = new Color(0xE5, 0xE7, 0xEB);

    private PlaceholderTextField dashboardSearch;
    private DefaultTableModel dashboardModel;
    private Timer dashboardDebounceTimer;

    public CreateDashboardPage() {
        setLayout(new BorderLayout());
        setBackground(Color.white);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JScrollPane tableScroll = createTableDashboard();
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(Color.white);
        top.add(createHeader());
        top.add(createToolbarDashboard());

        add(top, BorderLayout.NORTH);
        add(tableScroll, BorderLayout.CENTER);

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

    public void refresh() {
        loadDashboardData(Books.getAllBooks());
    }

        private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // --- Ligne du haut : le titre ---
        JLabel title = new JLabel("Library Dashboard");
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
                    ? Books.getAllBooks()
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


      private JScrollPane createTableDashboard() {
        String[] columns = { "ID", "Title", "Author", "Category", "Status" };

        // Modèle vide au départ
        dashboardModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Chargement des données depuis la base
        loadDashboardData(Books.getAllBooks());

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

    static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {

            // ⚠️ Normaliser en BookStatus
            BookStatus status;
            if (value instanceof BookStatus bookStatus) {
                status = bookStatus;
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

}
