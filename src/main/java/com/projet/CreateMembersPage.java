package com.projet;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.projet.LibrairiePage.DeleteEditor;
import com.projet.LibrairiePage.DeleteRenderer;
import com.projet.LibrairiePage.EditEditor;
import com.projet.LibrairiePage.EditRenderer;
import com.projet.LibrairiePage.StatusRenderer;


import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public class CreateMembersPage extends JPanel {
    private PlaceholderTextField memberSearch;
    private DefaultTableModel membersModel;
    private Timer membersDebounceTimer;
    private static final Color SIDEBAR_ACTIVE = new Color(0x3B, 0x82, 0xF6); // bleu actif
    private static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private static final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_GRAY = new Color(0xE5, 0xE7, 0xEB);
    

    /* public class CreateMembersPage(){

        } */

         private void loadMembersIntoModel(List<Member> members) {
        membersModel.setRowCount(0); // ⚠️ booksModel (camelCase)
        for (Member m : members) {
            membersModel.addRow(new Object[] {
                     m.getId(),
                    m.getFirstName(),
                    m.getLastName(),
                    m.getEmail(),
                    m.getPhone(),
                    m.getMembershipDate(),
                    m.getStatus(),
                    "",
                    ""
            });
        }
    }

        private JScrollPane createTableable(){
        String[] columns = { "ID", "First Name", "Last Name", "Email", "Phone",
                             "Membership Date", "Status", "Edit", "Delete" };

        membersModel = new DefaultTableModel(columns,0){
        @Override
        public boolean isCellEditable(int row,int column){
            return column == 7 || column ==8;
        }};

        loadMembersIntoModel(Members.getAllMembers());

         JTable table = new JTable(membersModel);
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
       // table.getColumnModel().getColumn(5).setCellEditor(new EditEditor(row -> onEditBook(table, row)));
        table.getColumnModel().getColumn(6).setCellRenderer(new DeleteRenderer());
       // table.getColumnModel().getColumn(6).setCellEditor(new DeleteEditor(row -> onDeleteBook(table, row)));

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
            MemberStatus status;
            if (value instanceof BookStatus) {
                status = (MemberStatus) value;
            } else if (value != null) {
                status = MemberStatus.fromString(value.toString());
            } else {
                status = MemberStatus.ACTIF;
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
                case ACTIF -> {
                    bg = new Color(0xD1, 0xFA, 0xE5);
                    fg = new Color(0x06, 0x5F, 0x46);
                }
                case SUSPENDU -> {
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
