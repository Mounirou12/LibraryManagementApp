package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Borrowing {

    public static void main(String[] args) {
        crudBorrowing();
    }

    public static void crudBorrowing() {
        String url = "jdbc:mysql://127.0.0.1:3306/Librairie";
        String username = "mounir";
        String password = "agnila10";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            String queryRD = "SELECT * From Borrowing";

            String queryCR = "INSERT INTO Borrowing(bookId,memberId,borrowDate,dueDate,returnDate,status)"
                    + " VALUES(?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(queryCR);
            pstmt.setInt(1, 5);
            pstmt.setInt(2, 3);
            pstmt.setObject(3, LocalDate.now());
            pstmt.setObject(4, LocalDate.now().plusDays(14));
            pstmt.setObject(5, null);
            pstmt.setString(6, "EN_COURS");

            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Rows insetrted" + rowsAffected);

            String queryUp = "UPDATE Borrowing SET status = ? WHERE id=?";
            pstmt = conn.prepareStatement(queryUp);
            pstmt.setString(1, "RETOURNE");
            pstmt.setInt(2, 1);
            pstmt.executeUpdate();

            String queryDl = "DELETE FROM Borrowing WHERE id = ?";
            pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, 2);

            pstmt.executeUpdate();
            rs = pstmt.executeQuery(queryRD);
            while (rs.next()) {
                System.out.print(rs.getInt(1) + " - ");
                System.out.print(rs.getString(2) + " - ");
                System.out.print(rs.getString(3) + " - ");
                System.out.print(rs.getString(4) + " - ");
                System.out.print(rs.getString(5) + " - ");
                System.out.print(rs.getDate(6) + " - ");
                System.out.println(rs.getString(7));
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null && !conn.isClosed())
                    conn.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }

}
