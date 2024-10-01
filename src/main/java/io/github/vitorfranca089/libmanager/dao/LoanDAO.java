package io.github.vitorfranca089.libmanager.dao;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.dto.LoanDTO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.Loan;

import java.sql.*;

public class LoanDAO {

    public LoanDTO makeLoan(Loan loan){
        String sqlInsert = "INSERT INTO loans (id_book, id_user, loan_date, return_date) VALUES (?,?,?,?)";
        String sqlUpdate = "UPDATE books SET is_available = 0 WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert,  PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
            stmtInsert.setInt(1, loan.getBook().getId());
            stmtInsert.setInt(2, loan.getUser().getId());
            stmtInsert.setTimestamp(3, Timestamp.valueOf(loan.getLoanDate()));
            stmtInsert.setTimestamp(4, Timestamp.valueOf(loan.getReturnDate()));
            stmtInsert.executeUpdate();

            stmtUpdate.setInt(1, loan.getBook().getId());
            stmtUpdate.executeUpdate();

            ResultSet rs = stmtInsert.getGeneratedKeys();

            if(rs.next()){
                UserDTO userDTO = new UserDTO(loan.getUser());
                BookDTO bookDTO = new BookDTO(loan.getBook());
                return new LoanDTO(
                        rs.getInt(1),
                        bookDTO,
                        userDTO,
                        loan.getLoanDate(),
                        loan.getReturnDate()
                );
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
