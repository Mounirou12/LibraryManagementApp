package com.projet;


public class Main {
    public static void main(String[] args) {
        Books tableBook = new Books();
        Members tablMembers = new Members();
        Borrowing tableBorrowing = new Borrowing();

        tableBook.start();
        tablMembers.start();
        tableBorrowing.start();
    }
}