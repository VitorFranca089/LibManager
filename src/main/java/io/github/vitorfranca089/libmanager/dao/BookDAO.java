package io.github.vitorfranca089.libmanager.dao;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.model.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BookDAO {

    public int addBook(Book book) {
        String sql = "INSERT INTO books (title, author, year_pub, genre, is_available) VALUES (?,?,?,?,?)";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)){
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYearPub());
            stmt.setString(4, book.getGenre());
            stmt.setInt(5, (book.isAvailable() ? 1 : 0));
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if(rs.next()){
                return rs.getInt(1);
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return -1;
    }

    public BookDTO findBookByID(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, id);

            ResultSet rs = stmt.executeQuery();

            if(rs.next()){
                return new BookDTO(
                        rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getString(5),
                        rs.getBoolean(6)
                );
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title = ?, author = ?, year_pub = ?, genre = ? WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setString(1, book.getTitle());
            stmt.setString(2, book.getAuthor());
            stmt.setInt(3, book.getYearPub());
            stmt.setString(4, book.getGenre());
            stmt.setInt(5, book.getId());

            return (stmt.executeUpdate()) > 0;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
