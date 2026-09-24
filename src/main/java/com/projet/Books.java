package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Books {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Librairie";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static int insertBook(String title, String author, String category, String status) {
        String queryCR = "INSERT INTO Books (title, author, category, status) "
                + "VALUES (?,?,?,?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryCR);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, category);
            pstmt.setString(4, status);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Book : " + e.getMessage());
            return 0;
        }
    }

    public static int updateBook(int id, String title, String author, String category, String status) {
        String queryUp = "UPDATE Books SET title=?,author=?,category=?,status=? WHERE id=?";// La requête SQL vers la
                                                                                            // base de donnees pour
                                                                                            // mettre a jour un livre
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryUp);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, category);
            pstmt.setString(4, status);
            pstmt.setInt(5, id);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur UPDATE Book : " + e.getMessage());
            return 0;
        }
    }

    public static int deleteBook(int id) {
        String queryDl = "DELETE FROM Books WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur DELETE Book : " + e.getMessage());
            return 0;
        }
    }

    public static void getMemberById(int id) {
        String sqlRdId = "SELECT id,title, author, category, status FROM Books WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(sqlRdId);
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.getInt(1) + " - "
                            + rs.getString(2) + " - "
                            + rs.getString(3) + " - "
                            + rs.getString(4) + " - "
                            + rs.getString(5));
                } else {
                    System.out.println("Aucun livre avec l'id " + id);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT Books : " + e.getMessage());
        }
    }

    public static void getAllMembers() {
        String queryRD = "SELECT * From Books";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryRD);
            ResultSet rs = pstmt.executeQuery(queryRD);
            while (rs.next()) {
                System.out.print(rs.getInt(1) + " - ");
                System.out.print(rs.getString(2) + " - ");
                System.out.print(rs.getString(3) + " - ");
                System.out.print(rs.getString(4) + " - ");
                System.out.println(rs.getString(5) + " - ");
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL Books : " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        // insertBook("L'Assassin royal", "Robin Hobb", "Fantasy", "DISPONIBLE");
        // updateBook(5, "Les Misérables", "Victor Hugo", "Classique", "EMPRUNTE");
        // deleteBook(5);
        getMemberById(1);
        // getAllMembers();
    }

}
