package com.projet;

import java.time.LocalDate;

public class Borrowing {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDate borrowDate;
    private LocalDate duDateDtDate;
    private LocalDate returDate;
    private BorrowingStatus status;

    public Borrowing(int id, int bookId, int memberId, LocalDate borrowDate, LocalDate duDateDtDate,
            LocalDate returDate, BorrowingStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.duDateDtDate = duDateDtDate;
        this.returDate = returDate;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public int getBookId() {
        return bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDuDateDtDate() {
        return duDateDtDate;
    }

    public LocalDate getReturDate() {
        return returDate;
    }

    public BorrowingStatus getStatus() {
        return status;
    }

}