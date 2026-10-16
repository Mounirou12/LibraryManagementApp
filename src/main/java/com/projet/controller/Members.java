package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Members extends Thread {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/Librairie";
    private static final String USERNAME = "mounir";
    private static final String PASSWORD = "agnila10";

    public static boolean insertMember(String firstName, String lastName, String email, String phone,
            LocalDate membershipDate, MemberStatus status) {
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
            pstmt.setString(6, status.name());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur INSERT Member : " + e.getMessage());
            return false;
        }
    }

    public static boolean updateMember(int id, String firstName, String lastName, String email, String phone,
            LocalDate membershipDate, MemberStatus status) {
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
            pstmt.setString(6, status.name());
            pstmt.setInt(7, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur UPDATE Member : " + e.getMessage());
            return false;
        }
    }

    public static boolean deleteMember(int id) {
        String queryDl = "DELETE FROM Members WHERE id = ?";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryDl);
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur DELETE Member : " + e.getMessage());
            return false;
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
                    System.out.println("Aucun membre avec l'id " + id);
                }
            }
        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT MEMBER : " + e.getMessage());
        }
    }

    public static List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String queryRD = "SELECT * From Members";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement pstmt = conn.prepareStatement(queryRD);
            ResultSet rs = pstmt.executeQuery(queryRD);
            while (rs.next()) {
                Member m = new Member(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getDate(6).toLocalDate(),
                        MemberStatus.fromString(rs.getString(7)));
                members.add(m);
            }

        } catch (SQLException | ClassNotFoundException e) {
            System.out.println("Erreur SELECT ALL MEMBERS : " + e.getMessage());
        }
        return members;

    }

    public static List<Member> searchMembers(String search) {
        List<Member> members = new ArrayList<>();
        String querySM = "SELECT * FROM Members "
                + "WHERE firstName LIKE CONCAT('%', ?, '%') "
                + "   OR lastName LIKE CONCAT('%', ?, '%')";
        try (Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                PreparedStatement pstmt = conn.prepareStatement(querySM)) {
            pstmt.setString(1, search);
            pstmt.setString(2, search);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    members.add(new Member(
                            rs.getInt("id"),
                            rs.getString("firstName"),
                            rs.getString("lastName"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getDate("membershipDate").toLocalDate(),
                            MemberStatus.fromString(rs.getString("status"))));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur recherche : " + e.getMessage());
        }
        return members;
    }

    @Override
    public void run() {
        // insertMember("Vincent", "Kompany", "vincentkompany@email.com",
        // "+22990875634", LocalDate.of(2023, 1, 1),MemberStatus.SUSPENDU);
        // insertMember("Amadou", "Diallo", "amadou.diallo@email.com", "+22997000001",
        // LocalDate.of(2024, 1, 15),MemberStatus.ACTIF);
        // updateMember(1, "Vincent", "Kompany", "vincentkompany@email.com",
        // "+22990875634", LocalDate.of(2023, 1, 1), MemberStatus.ACTIF);
        // deleteMember(6);
        // getMemberById(8);
        // getAllMembers();
    }
}
