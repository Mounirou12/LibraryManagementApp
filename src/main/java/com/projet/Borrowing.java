package com.projet;

import java.time.LocalDate;

public class Borrowing {
    private int id;
    private int bookId;
    private int memberId;
    private LocalDate borrowDate;
    private LocalDate duDateDtDate;
    private LocalDate returnDate;
    private BorrowingStatus status;

    public Borrowing(int id, int bookId, int memberId, LocalDate borrowDate, LocalDate duDateDtDate,
            LocalDate returnDate, BorrowingStatus status) {
        this.id = id;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.duDateDtDate = duDateDtDate;
        this.returnDate = returnDate;
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

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public BorrowingStatus getStatus() {
        return status;
    }

}