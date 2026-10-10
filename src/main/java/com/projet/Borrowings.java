package com.projet;

import java.sql.Connection;
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
                if (rs.next()) {
                    return "ACTIF".equals(rs.getString("status"));
                }
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erreur verification membre:" + e.getMessage());
            return false;
        }
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
        if (returnDate != null) {
            status = BorrowingStatus.RETOURNE;
        } else if (LocalDate.now().isAfter(dueDate)) {
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
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Borrowing : " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBorrowing(int id, int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate,
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
        if (returnDate != null) {
            status = BorrowingStatus.RETOURNE;
        } else if (LocalDate.now().isAfter(dueDate)) {
            status = BorrowingStatus.EN_RETARD;
        } else {
            status = BorrowingStatus.EN_COURS;
        }
        String queryUp = "UPDATE Borrowing SET bookId=?,memberId=?,borrowDate=?,dueDate=?, returnDate= ?, status = ? WHERE id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryUp);
            pstmt.setInt(1, bookId);
            pstmt.setInt(2, memberId);
            pstmt.setObject(3, borrowDate);
            pstmt.setObject(4, dueDate);
            pstmt.setObject(5, returnDate);
            pstmt.setString(6, status.name());
            pstmt.setInt(7, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur UPDATE Borrowing : " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteBorrowing(int id) {
        String queryDl = "DELETE FROM Borrowing WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur DELETE Borrowing : " + e.getMessage());
            return false;
        }
    }

    public void getBorrowingById(int id) {
        String sqlRdId = "SELECT id,bookId,memberId,borrowDate,dueDate,returnDate,status FROM Borrowing WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sqlRdId);
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getInt(1) + " - "
                            + rs.getInt(2) + " - "
                            + rs.getInt(3) + " - "
                            + rs.getDate(4) + " - "
                            + rs.getDate(5) + " - "
                            + rs.getDate(6) + " - "
                            + rs.getString(7));
                } else {
                    System.out.println("Aucun enprunt avec l'id " + id);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT Borrowing : " + e.getMessage());
        }
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
                Borrowing b = new Borrowing(rs.getInt(1),
                        rs.getInt(2),
                        rs.getInt(3),
                        rs.getDate(4).toLocalDate(),
                        rs.getDate(5).toLocalDate(),
                        rs.getDate(6).toLocalDate(),
                        BorrowingStatus.fromString(rs.getString(7)));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL Borrowing : " + e.getMessage());
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

    @Override
    public void run() {
        // insertBorrowing(1, 1, LocalDate.of(2024, 04, 1), LocalDate.of(2024, 04,
        // 1).plusDays(14), null);
        // insertBorrowing(1, 2, LocalDate.of(2024, 03, 10), LocalDate.of(2024, 03,
        // 10).plusDays(14),LocalDate.of(2024, 03, 22));
        // updateBorrowing(2, 4, 3, LocalDate.of(2024, 02, 1), LocalDate.of(2024, 02,
        // 1).plusDays(14), LocalDate.of(2024, 02, 22));
        // deleteBorrowing(1);
        // getBorrowingById(2);
        // getAllMembers();

    }

}
