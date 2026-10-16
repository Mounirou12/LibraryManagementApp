package com.projet.model;

import com.projet.status.BorrowingStatus;

import java.time.LocalDate;

public class BorrowingView {

    private int id;
    private int bookId;
    private String bookTitle;         
    private int memberId;
    private String memberFullName;    
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowingStatus status;
    public BorrowingView(int id, int bookId, String bookTitle, int memberId, String memberFullName,
            LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate, BorrowingStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.memberId = memberId;
        this.memberFullName = memberFullName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }
    public int getId() {
        return id;
    }
    public int getBookId() {
        return bookId;
    }
    public String getBookTitle() {
        return bookTitle;
    }
    public int getMemberId() {
        return memberId;
    }
    public String getMemberFullName() {
        return memberFullName;
    }
    public LocalDate getBorrowDate() {
        return borrowDate;
    }
    public LocalDate getDueDate() {
        return dueDate;
    }
    public LocalDate getReturnDate() {
        return returnDate;
    }
    public BorrowingStatus getStatus() {
        return status;
    }

}
