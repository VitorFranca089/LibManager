package io.github.vitorfranca089.libmanager.dao;

import io.github.vitorfranca089.libmanager.config.DatabaseConfig;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.dto.LoanDTO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.Loan;
import io.github.vitorfranca089.libmanager.model.enums.LoanStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanDAO {

    public LoanDTO makeLoan(Loan loan){
        String sqlInsert = "INSERT INTO loans (id_book, id_user, loan_date, due_date) VALUES (?,?,?,?)";
        String sqlUpdate = "UPDATE books SET is_available = 0 WHERE id = ?";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert,  PreparedStatement.RETURN_GENERATED_KEYS);
            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate)){
            stmtInsert.setInt(1, loan.getBook().getId());
            stmtInsert.setInt(2, loan.getUser().getId());
            stmtInsert.setTimestamp(3, Timestamp.valueOf(loan.getLoanDate()));
            stmtInsert.setTimestamp(4, Timestamp.valueOf(loan.getDueDate()));
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
                        loan.getDueDate(),
                        loan.getReturnDate(),
                        loan.getLoanStatus()
                );
            }

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public List<LoanDTO> findLoansByUser(UserDTO user) {
        String sql = "SELECT * FROM loans INNER JOIN books ON books.id=loans.id_book WHERE loans.id_user = ? AND books.is_available = 0";
        try(Connection conn = DatabaseConfig.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)){
            stmt.setInt(1, user.id());

            ResultSet rs = stmt.executeQuery();
            List<LoanDTO> userLoans = new ArrayList<>();

            if(rs != null){
                while(rs.next()){
                    Optional<Timestamp> returnDate = Optional.ofNullable(rs.getTimestamp("return_date"));
                    LoanDTO loan = new LoanDTO(
                            rs.getInt("id"),
                            new BookDTO(
                                    rs.getInt("id_book"),
                                    rs.getString("title"),
                                    rs.getString("author"),
                                    rs.getInt("year_pub"),
                                    rs.getString("genre"),
                                    rs.getBoolean("is_available")
                            ),
                            user,
                            rs.getTimestamp("loan_date").toLocalDateTime(),
                            rs.getTimestamp("due_date").toLocalDateTime(),
                            returnDate.map(Timestamp::toLocalDateTime).orElse(null),
                            LoanStatus.valueOf(rs.getString("loan_status"))
                    );
                    userLoans.add(loan);
                }
            }

            return userLoans;

        }catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }

}
