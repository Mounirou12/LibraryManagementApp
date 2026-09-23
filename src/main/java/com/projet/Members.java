package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Members {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Librairie";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static int insertMember(String firstName, String lastName, String email, String phone,
            LocalDate membershipDate, String status,int year, int month, int day) {
                membershipDate = LocalDate.of(year,month,day);
        String queryCR = "INSERT INTO Members(firstName,lastName,email,phone,membershipDate,status)"
                + " VALUES(?,?,?,?,?,?)";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryCR);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setObject(5, membershipDate);
            pstmt.setString(6, status);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Member : " + e.getMessage());
            return 0;
        }
    }

    public static int updateMember(int id, String firstName, String lastName, String email, String phone,
            LocalDate membershipDate, String status,int year, int month, int day) {
                membershipDate = LocalDate.of(year,month,day);
        String queryUp = "UPDATE Members SET firstName=?,lastName=?,email=?,phone=?,membershipDate=?, status = ? WHERE id=?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryUp);
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, email);
            pstmt.setString(4, phone);
            pstmt.setObject(5, membershipDate);
            pstmt.setString(6, status);
            pstmt.setInt(7, id);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur UPDATE Member : " + e.getMessage());
            return 0;
        }
    }

    public static int deleteMember(int id) {
        String queryDl = "DELETE FROM Members WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate();

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur DELETE Member : " + e.getMessage());
            return 0;
        }
    }

    public static void getMemberById(int id) {
        String sqlRdId = "SELECT id,firstName,lastName,email,phone,membershipDate,status FROM Members WHERE id = ?";
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
                            + rs.getString(5) + " - "
                            + rs.getDate(6) + " - "
                            + rs.getString(7));
                } else {
                    System.out.println("Aucun livre avec l'id " + id);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT MEMBER : " + e.getMessage());
        }
    }

    public static void getAllMembers() {
        String queryRD = "SELECT * From Members";
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
                System.out.print(rs.getString(5) + " - ");
                System.out.print(rs.getDate(6) + " - ");
                System.out.println(rs.getString(7));
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL MEMBERS : " + e.getMessage());
        }

    }

    public static void main(String[] args) {
        insertMember("Vincent", "Kompany", "vincentkompany@email.com", "+22990875634", LocalDate.now(), "SUSPENDU", 2023, 1, 1);
        updateMember(4, "Diallo", "Amadou", "amadoutraore@email.com", "+22997676534", LocalDate.now(), "ACTIF", 2023, 1, 1);
        deleteMember(6);
        getMemberById(8);
        getAllMembers();
    }
}
