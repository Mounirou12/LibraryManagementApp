package com.projet;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

public class LibrairiePage extends JFrame {
    private static final Color SIDEBAR_ACTIVE = new Color(0x3B, 0x82, 0xF6); // bleu actif
    private static final Color TEXT_DARK = new Color(0x1F, 0x29, 0x37);
    private static final Color TEXT_GRAY = new Color(0x6B, 0x72, 0x80);
    private static final Color BORDER_GRAY = new Color(0xE5, 0xE7, 0xEB);
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

        contentArea.add(new CreateDashboardPage(), "dashboard");
        contentArea.add(new CreateBookspage(), "books");
        contentArea.add(new CreateMembersPage(), "members");
        contentArea.add(new CreateBorrowingsPage(), "borrowing");
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


    private JTable table;














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
