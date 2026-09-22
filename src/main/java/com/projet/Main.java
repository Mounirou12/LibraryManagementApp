package com.projet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        String url = "jdbc:mysql://127.0.0.1:3306/Librairie"; // Remplacez par l'URL de votre base de données
        String username = "mounir";// Remplacez par le nom d'utilisateur de votre base de données
        String password = "agnila10";// Remplacez par le mot de passe de votre base de données

        Connection conn = null;// La connexion vers la base de données

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");// Chargement du pilote JDBC
            System.out.println("Driver loaded successfully");// message de confirmation de chargement du pilote

            conn = DriverManager.getConnection(url, username, password);// Création de la connexion vers la base de données
            System.out.println("Connection established successfully");// message de confirmation de connexion vers la base de données
        } catch (ClassNotFoundException e) {// Gestion des exceptions de chargement du pilote
            System.out.println("Driver not found " + e.getMessage());// message d'erreur de chargement du pilote
        } catch (SQLException e) {// Gestion des exceptions de connexion
            System.out.println("Connection failed" + e.getMessage());// message d'erreur de connexion
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {// Fermeture de la connexion
                    conn.close();// Fermeture de la connexion
                    System.out.println("Connection closed");// message de confirmation de fermeture de la connexion
                }
            } catch (SQLException e) {// Gestion des exceptions de fermeture de la connexion
                System.out.println("Error closing connection: " + e.getMessage());// message d'erreur de fermeture de la connexion
            }
        }
    }
}