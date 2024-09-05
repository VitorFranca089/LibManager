package io.github.vitorfranca089.libmanager.model;

import java.time.LocalDateTime;

public class Loan {

    private int id;
    private Book id_book;
    private User id_user;
    private LocalDateTime loan_date;
    private LocalDateTime return_date;

    public Loan() {
    }

    public Loan(Book id_book, User id_user, LocalDateTime loan_date, LocalDateTime return_date) {
        this.id_book = id_book;
        this.id_user = id_user;
        this.loan_date = loan_date;
        this.return_date = return_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Book getId_book() {
        return id_book;
    }

    public void setId_book(Book id_book) {
        this.id_book = id_book;
    }

    public User getId_user() {
        return id_user;
    }

    public void setId_user(User id_user) {
        this.id_user = id_user;
    }

    public LocalDateTime getLoan_date() {
        return loan_date;
    }

    public void setLoan_date(LocalDateTime loan_date) {
        this.loan_date = loan_date;
    }

    public LocalDateTime getReturn_date() {
        return return_date;
    }

    public void setReturn_date(LocalDateTime return_date) {
        this.return_date = return_date;
    }

}