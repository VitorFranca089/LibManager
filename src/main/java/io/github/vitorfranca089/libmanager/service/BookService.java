package io.github.vitorfranca089.libmanager.service;

import io.github.vitorfranca089.libmanager.dao.BookDAO;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.model.Book;
import io.github.vitorfranca089.libmanager.util.UpdateUtils;

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

        UpdateUtils.updateIfNotEmpty(newTitle, bookToUpdate::setTitle);
        UpdateUtils.updateIfNotEmpty(newAuthor, bookToUpdate::setAuthor);
        UpdateUtils.updateIfValidYear(newYearPub, bookToUpdate::setYearPub);
        UpdateUtils.updateIfNotEmpty(newGenre, bookToUpdate::setGenre);

        return bookDAO.updateBook(bookToUpdate);
    }

    public boolean deleteBook(int id) {
        return bookDAO.deleteBook(id);
    }
}
