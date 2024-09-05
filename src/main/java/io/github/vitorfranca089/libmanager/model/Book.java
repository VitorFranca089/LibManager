package io.github.vitorfranca089.libmanager.model;

public class Book {

    private int id;
    private String title;
    private String author;
    private int year_pub;
    private String genre;
    private boolean isAvailable;

    public Book() {
    }

    public Book(String title, String author, int year_pub, String genre, boolean isAvailable) {
        this.title = title;
        this.author = author;
        this.year_pub = year_pub;
        this.genre = genre;
        this.isAvailable = isAvailable;
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

    public int getYear_pub() {
        return year_pub;
    }

    public void setYear_pub(int year_pub) {
        this.year_pub = year_pub;
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