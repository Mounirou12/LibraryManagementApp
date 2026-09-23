package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Members {
    public static void main(String[] args) {
        crudMembers();
    }

    public static void crudMembers() {
        String url = "jdbc:mysql://127.0.0.1:3306/Librairie";
        String username = "mounir";
        String password = "agnila10";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(url, username, password);
            String queryRD = "SELECT * From Members";
            /*
             * String queryCR =
             * "INSERT INTO Members(firstName,lastName,email,phone,membershipDate,status)"
             * + " VALUES(?,?,?,?,?,?)";
             * pstmt.setString(1, "Traore");
             * pstmt.setString(2, "Amadou");
             * pstmt.setString(3, "amadoutraore@email.com");
             * pstmt.setString(4, "+22997676534");
             * pstmt.setObject(5, LocalDate.now());
             * pstmt.setString(6, "SUSPENDU");
             * 
             * int rowsAffected = pstmt.executeUpdate();
             * System.out.println("Rows insetrted" + rowsAffected);
             */
            String queryUp = "UPDATE Members SET status = ? WHERE id=?";
            pstmt = conn.prepareStatement(queryUp);
            pstmt.setString(1, "ACTIF");
            pstmt.setInt(2, 4);
            pstmt.executeUpdate();

            String queryDl = "DELETE FROM Members WHERE id = ?";
            pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, 5);

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
