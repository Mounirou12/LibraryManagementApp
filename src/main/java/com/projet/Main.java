package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/Librairie"; // Remplacez par l'URL de votre base de données
        String username = "mounir";// Remplacez par le nom d'utilisateur de votre base de données
        String password = "agnila10";// Remplacez par le mot de passe de votre base de données

        Connection conn = null;// La connexion vers la base de données
        Statement stmt = null;// Le statement SQL vers la base de données
        ResultSet rs = null;// Le résultat de la requête SQL

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");// Chargement du pilote JDBC
            // System.out.println("Driver loaded successfully");// message de confirmation
            // de chargement du pilote

            conn = DriverManager.getConnection(url, username, password);// Création de la connexion vers la base de
                                                                        // données
            System.out.println("Connection established successfully");// message de confirmation de connexion vers la
                                                                      // base de données

            String query = "SELECT title FROM Books WHERE id=1";// La requête SQL vers la base de données
            stmt = conn.createStatement();// Création du statement SQL vers la base de données
            rs = stmt.executeQuery(query);// Exécuter la requête SQL vers la base de données

            if (rs.next()) {// Si le resultat de la requête SQL est trouvé
                String title = rs.getString("title");// Recupérer le titre du livre
                System.out.println("Titre du livre: " + title);// Afficher le titre du livre
            } else {
                System.out.println("No book found with id = 1");// message de confirmation de chargement du pilote
            }
        } catch (Exception e) {// Gestion des exceptions de chargement du pilote
            System.out.println("Error: " + e.getMessage());// message d'erreur de chargement du pilote
            e.printStackTrace();// Afficher le stack trace de l'exception
        } finally {
            try {
                if (rs != null)
                    rs.close();// Fermeture du resultSet
                if (stmt != null)
                    stmt.close();// Fermeture du statement
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