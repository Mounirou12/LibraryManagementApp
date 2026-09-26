package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Locale;

public class Borrowing {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Librairie";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static int insertBorrowing(int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate,
            Locale returnDate, BorrowingStatus status, int year, int month, int day) {
        borrowDate = LocalDate.of(year, month, day);
        dueDate = borrowDate.plusDays(14);
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
            return pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Borrowing : " + e.getMessage());
            return 0;
        }
    }

    public static int updateBorrowing(int id, int bookId, int memberId, LocalDate borrowDate, LocalDate dueDate,
            Locale returnDate, BorrowingStatus status, int year, int month, int day) {
        borrowDate = LocalDate.of(year, month, day);
        dueDate = borrowDate.plusDays(14);
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
            return pstmt.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur UPDATE Borrowing : " + e.getMessage());
            return 0;
        }
    }

    public static int deleteBorrowing(int id) {
        String queryDl = "DELETE FROM Borrowing WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur DELETE Borrowing : " + e.getMessage());
            return 0;
        }
    }

    public static void getBorrowingById(int id) {
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

    public static void getAllMembers() {
        String queryRD = "SELECT * From Borrowing";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryRD);
            ResultSet rs = pstmt.executeQuery(queryRD);
            while (rs.next()) {
                System.out.print(rs.getInt(1) + " - ");
                System.out.print(rs.getInt(2) + " - ");
                System.out.print(rs.getInt(3) + " - ");
                System.out.print(rs.getDate(4) + " - ");
                System.out.print(rs.getDate(5) + " - ");
                System.out.print(rs.getDate(6) + " - ");
                System.out.println(rs.getString(7));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL Borrowing : " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        insertBorrowing(5, 8, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowingStatus.EN_COURS, 2023, 1, 1);
        updateBorrowing(7, 5, 8, LocalDate.now(), LocalDate.now().plusDays(14), null, BorrowingStatus.EN_RETARD, 2023, 1, 1);
       // deleteBorrowing(4);
        //getBorrowingById(4);
        getAllMembers();
    }

  
}
