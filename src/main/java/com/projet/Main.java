package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/Librairie"; // Remplacez par l'URL de votre base de données
        String username = "mounir";// Remplacez par le nom d'utilisateur de votre base de données
        String password = "agnila10";// Remplacez par le mot de passe de votre base de données

        Connection conn = null;// La connexion vers la base de données
        PreparedStatement pstmt = null;// Le statement SQL vers la base de données
        ResultSet rs = null;// Le résultat de la requête SQL

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");// Chargement du pilote JDBC
            // System.out.println("Driver loaded successfully");// message de confirmation
            // de chargement du pilote

            conn = DriverManager.getConnection(url, username, password);// Création de la connexion vers la base de
                                                                        // données
            System.out.println("Connection established successfully");// message de confirmation de connexion vers la
                                                                      // base de données

            String query = "SELECT * FROM Books";// La requête SQL vers la base de données
            // String queryIn = "INSERT INTO Books (title, author, category, status) "
            // + "VALUES ('Les Misérables', 'Victor Hugo', 'Classique', 'EMPRUNTE')";// La requête SQL vers la base de donnees pour ajouter un livre
            String queryUp = "UPDATE Books SET title=? WHERE id=?";// La requête SQL vers la base de donnees pour mettre a jour un livre
          //  String sqlDl = "DELETE FROM Books WHERE ID = ?";// La requête SQL vers la base de données pour supprimer un livre
            pstmt = conn.prepareStatement(queryUp);// Création du statement SQL vers la base de données
            // boolean status = stmt.execute(queryIn);// Exécuter la requête SQL vers la base de données
            // System.out.println("Execute status: " + status); // Afficher le statut de l'execution de la requête SQL vers la base de données
            pstmt.setString(1, "Pere riche");
            pstmt.setInt(2, 1);
            pstmt.executeUpdate();// Exécuter la requête SQL vers la base de données
            //int rowsDeleted = pstmt.executeUpdate(sqlDl);// Exécuter la requête SQL vers la base de données
            rs = pstmt.executeQuery(query);// Exécuter la requête SQL vers la base de données
            //System.out.println("Rows Updated: "+rowsUpdate);
            //System.out.println("Rows deleted: " + rowsDeleted);// Afficher le nombre de lignes affectées par la requête SQL vers la base de données

            while (rs.next()) {
                System.out.print(rs.getInt(1) + " - ");
                System.out.print(rs.getString(2) + " - ");
                System.out.print(rs.getString(3) + " - ");
                System.out.print(rs.getString(4) + " - ");
                System.out.println(rs.getString(5));
            }
        } catch (Exception e) {// Gestion des exceptions de chargement du pilote
            System.out.println("Error: " + e.getMessage());// message d'erreur de chargement du pilote
            e.printStackTrace();// Afficher le stack trace de l'exception
        } finally {
            try {
                if (rs != null)
                    rs.close();// Fermeture du resultSet
                if (pstmt != null)
                    pstmt.close();// Fermeture du statement
                if (conn != null && !conn.isClosed()) {// Fermeture de la connexion
                    conn.close();// Fermeture de la connexion
                    System.out.println("Connection closed");// message de confirmation de fermeture de la connexion
                }
            } catch (SQLException e) {// Gestion des exceptions de fermeture de la connexion
                System.out.println("Error closing connection: " + e.getMessage());// message d'erreur de fermeture de la
                                                                                  // connexion
            }
        }
    }
}