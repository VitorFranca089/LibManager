package io.github.vitorfranca089.libmanager.service;

import io.github.vitorfranca089.libmanager.dao.BookDAO;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.model.Book;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class BookService {

    private final BookDAO bookDAO = new BookDAO();

    public int addBook(String name, String author, int pubYear, String genre) {
        Book book = new Book(name, author, pubYear, genre, true);
        return bookDAO.addBook(book);
    }

    public BookDTO findBookById(int id){
        return bookDAO.findBookByID(id);
    }

    public boolean updateBook(BookDTO book, String newTitle, String newAuthor, int newYearPub, String newGenre) {
        Book bookToUpdate = new Book(book);

        updateIfNotEmpty(newTitle, bookToUpdate::setTitle);
        updateIfNotEmpty(newAuthor, bookToUpdate::setAuthor);
        updateIfValidYear(newYearPub, bookToUpdate::setYearPub);
        updateIfNotEmpty(newGenre, bookToUpdate::setGenre);

        return bookDAO.updateBook(bookToUpdate);
    }

    private void updateIfNotEmpty(String newValue, Consumer<String> setter) {
        Optional.ofNullable(newValue)
                .filter(value -> !value.isBlank())
                .ifPresent(setter);
    }

    private void updateIfValidYear(int newValue, IntConsumer setter) {
        if (newValue > 0) {
            setter.accept(newValue);
        }
    }

    public boolean deleteBook(int id) {
        return bookDAO.deleteBook(id);
    }
}
