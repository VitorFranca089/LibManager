package io.github.vitorfranca089.libmanager.dto;

import io.github.vitorfranca089.libmanager.model.Book;

public record BookDTO(
        int id,
        String title,
        String author,
        int yearPub,
        String genre,
        boolean isAvailable
) {
    public BookDTO(Book book){
        this(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getYearPub(),
                book.getGenre(),
                book.isAvailable()
        );
    }
}
