package io.github.vitorfranca089.libmanager.service;

import io.github.vitorfranca089.libmanager.dao.BookDAO;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.model.Book;

public class BookService {

    private final BookDAO bookDAO = new BookDAO();

    public int addBook(String name, String author, int pubYear, String genre) {
        Book book = new Book(name, author, pubYear, genre, true);
        return bookDAO.addBook(book);
    }

    public BookDTO findBookById(int id){
        return bookDAO.findBookByID(id);
    }

}
