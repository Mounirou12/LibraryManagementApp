package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Books extends Thread {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Librairie";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public int insertBook(String title, String author, String category, BookStatus status) {
        String queryCR = "INSERT INTO Books (title, author, category, status) "
                + "VALUES (?,?,?,?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryCR);
            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setString(3, category);
            pstmt.setString(4, status.name());
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Book : " + e.getMessage());
            return 0;
        }
    }

    public int updateBook(int id, String title, String author, String category, BookStatus status) {
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
            pstmt.setString(4, status.name());
            pstmt.setInt(5, id);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur UPDATE Book : " + e.getMessage());
            return 0;
        }
    }

    public static boolean deleteBook(int id) {
        String queryDl = "DELETE FROM Books WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() >0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur DELETE Book : " + e.getMessage());
            return false;
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

    public static List<Book> getAllMembers() {
        List<Book> books = new ArrayList<>();
        String queryRD = "SELECT * From Books";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(queryRD);
                ResultSet rs = pstmt.executeQuery()) {

            Class.forName("com.mysql.cj.jdbc.Driver");
            while (rs.next()) {
                Book b = new Book(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5));
                books.add(b);
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL Books : " + e.getMessage());
        }
        return books;

    }

    public static List<Book> searchBooks(String search) {
        List<Book> books = new ArrayList<>();
        String querySB = "SELECT * FROM Books "
                + "WHERE title LIKE CONCAT('%', ?, '%') "
                + "   OR author LIKE CONCAT('%', ?, '%')";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(querySB)) {
     

            pstmt.setString(1, search);
            pstmt.setString(2, search);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    books.add(new Book(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("author"),
                            rs.getString("category"),
                            rs.getString("status")));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }
        return books;
    }

    @Override
    public void run() {
        // insertBook("L'Assassin royal", "Robin Hobb", "Fantasy",
        // BookStatus.DISPONIBLE);
        // insertBook("1984", "George Orwell", "Science-Fiction", BookStatus.EMPRUNTE);
        // insertBook("Harry Potter à l''école des sorciers", "J.K. Rowling", "Fantasy",
        // BookStatus.RESERVE);
        // updateBook(1,"L'Assassin royal", "Robin Hobb", "Fantasy",
        // BookStatus.EMPRUNTE);
        // deleteBook(5);
        // getMemberById(1);
        // getAllMembers();

    }

}
