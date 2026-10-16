package com.projet;

public class Book {
    private int id;
    private String title;
    private String author;
    private String category;
    private BookStatus status;

    public Book(int id, String title, String author, String category, BookStatus status) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.category = category;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public BookStatus getStatus() {
        return status;
    }

}
