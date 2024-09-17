package io.github.vitorfranca089.libmanager.model;

import io.github.vitorfranca089.libmanager.dto.BookDTO;

public class Book {

    private int id;
    private String title;
    private String author;
    private int yearPub;
    private String genre;
    private boolean isAvailable;

    public Book() {
    }

    public Book(String title, String author, int yearPub, String genre, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.yearPub = yearPub;
        this.genre = genre;
        this.isAvailable = isAvailable;
    }

    public Book(BookDTO bookDTO){
        this.id = bookDTO.id();
        this.title = bookDTO.title();
        this.author = bookDTO.author();
        this.yearPub = bookDTO.yearPub();
        this.genre = bookDTO.genre();
        this.isAvailable = bookDTO.isAvailable();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYearPub() {
        return yearPub;
    }

    public void setYearPub(int yearPub) {
        this.yearPub = yearPub;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

}