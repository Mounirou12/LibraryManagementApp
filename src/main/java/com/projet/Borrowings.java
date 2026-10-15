package com.projet;

import java.sql.*;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Borrowings extends Thread {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Librairie";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean canBorrow(int memberId) {
        String sqlRSM = "SELECT status FROM Members WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sqlRSM)) {
            pstmt.setInt(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Membre " + memberId + " introuvable.");
                    return false;
                }

                String status = rs.getString("status");
                if (!"ACTIF".equals(status)) {
                    System.out.println("Membre " + memberId
                            + " non actif (statut : " + status + ").");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur vérification membre : " + e.getMessage());
            return false;
        }

        // 2. Vérifier que le membre n'a pas déjà 2 emprunts en cours
        String sqlCount = "SELECT COUNT(*) FROM Borrowing "
                + "WHERE memberId = ? AND status = 'EN_COURS'";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sqlCount)) {

            pstmt.setInt(1, memberId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count >= 2) {
                        System.out.println("Membre " + memberId
                                + " a déjà " + count + " emprunts en cours (max 2).");
                        return false;
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur comptage emprunts : " + e.getMessage());
            return false;
        }
        // 3. Tout est OK
        return true;
    }

    public static boolean isBookAvailable(int bookId) {
        String sqlRSBK = "SELECT status FROM Books WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(sqlRSBK)) {
            pstmt.setInt(1, bookId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return "DISPONIBLE".equals(rs.getString("status"));
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur vérification livre : " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBookStatut(int bookId, BookStatus status) {
        String queryUBS = "UPDATE Books SET status = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(queryUBS)) {
            pstmt.setString(1, status.name());
            pstmt.setInt(2, bookId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Erreur updateBookStatus : " + e.getMessage());
            return false;
        }
    }

    public static boolean insertBorrowing(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate,
            LocalDate returnDate) {
        if (!canBorrow(memberId)) {
            System.out.println("Emprunt refusé : le membre " + memberId
                    + " n'est pas autorisé (suspendu).");
            return false;
        }
        if (!isBookAvailable(bookId)) {
            System.out.println("Emprunt refusé : le livre " + bookId
                    + " n'est pas disponible.");
            return false;
        }

        BorrowingStatus status;
        if (returnDate != null && !LocalDate.now().isAfter(returnDate)) {
            status = BorrowingStatus.RETOURNE;
        } else if (returnDate != null && LocalDate.now().isAfter(returnDate)) {
            status = BorrowingStatus.EN_RETARD;
        } else {
            status = BorrowingStatus.EN_COURS;
        }
        String queryCR = "INSERT INTO Borrowing(bookId,memberId,borrowDate,dueDate,returnDate,status)"
                + " VALUES(?,?,?,?,?,?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryCR);
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setObject(3, borrowDate);
            pstmt.setObject(4, dueDate);
            pstmt.setObject(5, returnDate);
            pstmt.setString(6, status.name());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                updateBookStatut(bookId, BookStatus.EMPRUNTE);
                return true;
            }
            return false;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Borrowing : " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBorrowing(int id, int bookId, int memberId,
            LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {

        // ⚠️ Pas de canBorrow() ici : on modifie un emprunt existant

        // Calculer le statut
        BorrowingStatus status;
        if (returnDate != null) {
            // Le livre a été retourné → comparer returnDate avec dueDate
            if (returnDate.isAfter(dueDate)) {
                status = BorrowingStatus.EN_RETARD; // retourné en retard
            } else {
                status = BorrowingStatus.RETOURNE; // retourné à temps
            }
        } else {
            // Pas encore retourné → comparer aujourd'hui avec dueDate
            if (LocalDate.now().isAfter(dueDate)) {
                status = BorrowingStatus.EN_RETARD; // pas rendu et en retard
            } else {
                status = BorrowingStatus.EN_COURS; // pas rendu, dans les temps
            }
        }

        String queryUp = "UPDATE Borrowing SET bookId = ?, memberId = ?, "
                + "borrowDate = ?, dueDate = ?, returnDate = ?, status = ? "
                + "WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(queryUp)) {

            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setObject(3, borrowDate);
            pstmt.setObject(4, dueDate);
            pstmt.setObject(5, returnDate); // peut être null
            pstmt.setString(6, status.name());
            pstmt.setInt(7, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                updateBookStatut(bookId, BookStatus.DISPONIBLE);
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.out.println("Erreur UPDATE Borrowing : " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteBorrowing(int id) {
        // 1. Récupérer l'emprunt avant suppression
        Borrowing borrowing = getBorrowingById(id);
        if (borrowing == null) return false;

        // 2. Supprimer
        String queryDl = "DELETE FROM Borrowing WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(queryDl)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                // 3. Si l'emprunt était en cours, libérer le livre
                if (borrowing.getReturnDate() == null) {
                    Borrowings.updateBookStatut(borrowing.getBookId(), BookStatus.DISPONIBLE);
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.out.println("Erreur DELETE Borrowing : " + e.getMessage());
            return false;
        }
    }

    public static Borrowing getBorrowingById(int id) {
        String sqlRdId = "SELECT id, bookId, memberId, borrowDate, dueDate, returnDate, status "
                + "FROM Borrowing WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sqlRdId)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // ⚠️ Récupérer les dates brutes d'abord
                    java.sql.Date sqlBorrow = rs.getDate("borrowDate");
                    java.sql.Date sqlDue = rs.getDate("dueDate");
                    java.sql.Date sqlReturn = rs.getDate("returnDate");

                    // ⚠️ Construire et retourner l'objet
                    return new Borrowing(
                            rs.getInt("id"),
                            rs.getInt("bookId"),
                            rs.getInt("memberId"),
                            sqlBorrow != null ? sqlBorrow.toLocalDate() : null,
                            sqlDue != null ? sqlDue.toLocalDate() : null,
                            sqlReturn != null ? sqlReturn.toLocalDate() : null,
                            BorrowingStatus.fromString(rs.getString("status")));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur getBorrowingById : " + e.getMessage());
        }
        return null;   // ⚠️ Retourner null si introuvable
    }
    public List<Borrowing> getAllBorrowings() {
        List<Borrowing> borrowings = new ArrayList<>();
        String queryRD = "SELECT * From Borrowing";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryRD);
            ResultSet rs = pstmt.executeQuery(queryRD);
            while (rs.next()) {
                borrowings.add(new Borrowing(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDate(4).toLocalDate(),
                        rs.getDate(5).toLocalDate(),
                        rs.getDate(6).toLocalDate(),
                        BorrowingStatus.fromString(rs.getString(7))));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL Borrowing : " + e.getMessage());
        }
        return borrowings;

    }

    public static List<BorrowingView> getAllBorrowingsView() {
        List<BorrowingView> borrowings = new ArrayList<>();

        String query = "SELECT b.id, b.bookId, bk.title AS bookTitle, "
                + "       b.memberId, "
                + "       CONCAT(m.firstName, ' ', m.lastName) AS memberFullName, "
                + "       b.borrowDate, b.dueDate, b.returnDate, b.status "
                + "FROM Borrowing b "
                + "JOIN Books bk ON b.bookId = bk.id "
                + "JOIN Members m ON b.memberId = m.id "
                + "ORDER BY b.borrowDate DESC";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {

                java.sql.Date sqlBorrow = rs.getDate("borrowDate");
                java.sql.Date sqlDue = rs.getDate("dueDate");
                java.sql.Date sqlReturn = rs.getDate("returnDate");

                borrowings.add(new BorrowingView(
                        rs.getInt("id"),
                        rs.getInt("bookId"),
                        rs.getString("bookTitle"),
                        rs.getInt("memberId"),
                        rs.getString("memberFullName"),
                        sqlBorrow != null ? sqlBorrow.toLocalDate() : null,
                        sqlDue != null ? sqlDue.toLocalDate() : null,
                        sqlReturn != null ? sqlReturn.toLocalDate() : null,
                        BorrowingStatus.fromString(rs.getString("status"))));
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAllBorrowingsView : " + e.getMessage());
        }
        return borrowings;
    }

    public static List<Borrowing> searchBorrowings(String search) {
        List<Borrowing> borrowings = new ArrayList<>();
        String querySB = "SELECT * from Borrowing WHERE status = ?";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(querySB)) {
            pstmt.setString(1, search);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    borrowings.add(new Borrowing(
                            rs.getInt("id"),
                            rs.getInt("bookId"),
                            rs.getInt("memberId"),
                            rs.getDate("borrowDate").toLocalDate(),
                            rs.getDate("dueDate").toLocalDate(),
                            rs.getDate("returnDate").toLocalDate(),
                            BorrowingStatus.fromString(rs.getString("status"))));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }
        return borrowings;
    }

    public static List<BorrowingView> searchBorrowingsByStatus(BorrowingStatus status) {
        List<BorrowingView> borrowings = new ArrayList<>();

        String query = "SELECT b.id, b.bookId, bk.title AS bookTitle, "
                + "       b.memberId, "
                + "       CONCAT(m.firstName, ' ', m.lastName) AS memberFullName, "
                + "       b.borrowDate, b.dueDate, b.returnDate, b.status "
                + "FROM Borrowing b "
                + "JOIN Books bk ON b.bookId = bk.id "
                + "JOIN Members m ON b.memberId = m.id "
                + "WHERE b.status = ? "
                + "ORDER BY b.borrowDate DESC";

        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status.name());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date sqlBorrow = rs.getDate("borrowDate");
                    java.sql.Date sqlDue = rs.getDate("dueDate");
                    java.sql.Date sqlReturn = rs.getDate("returnDate");

                    borrowings.add(new BorrowingView(
                            rs.getInt("id"),
                            rs.getInt("bookId"),
                            rs.getString("bookTitle"),
                            rs.getInt("memberId"),
                            rs.getString("memberFullName"),
                            sqlBorrow != null ? sqlBorrow.toLocalDate() : null,
                            sqlDue != null ? sqlDue.toLocalDate() : null,
                            sqlReturn != null ? sqlReturn.toLocalDate() : null,
                            BorrowingStatus.fromString(rs.getString("status"))));
                }
            }

        } catch (SQLException e) {
            System.out.println("Erreur searchBorrowingsByStatus : " + e.getMessage());
        }
        return borrowings;
    }

    @Override
    public void run() {
        // insertBorrowing(2, 5, LocalDate.of(2024, 02, 10), LocalDate.of(2024, 02,
        // 10).plusDays(14),
        // null);
        // insertBorrowing(1, 2, LocalDate.of(2024, 03, 10), LocalDate.of(2024, 03,
        // 10).plusDays(14),LocalDate.of(2024, 03, 22));
        updateBorrowing(8, 2, 5, LocalDate.of(2024, 02, 10), LocalDate.of(2024, 02,
                10).plusDays(14), LocalDate.of(2024, 02, 22));
        // deleteBorrowing(1);
        // getBorrowingById(2);
        // getAllMembers();

    }

}
