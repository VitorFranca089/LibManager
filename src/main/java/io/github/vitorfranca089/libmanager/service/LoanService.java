package io.github.vitorfranca089.libmanager.service;

import io.github.vitorfranca089.libmanager.dao.LoanDAO;
import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.dto.LoanDTO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.Book;
import io.github.vitorfranca089.libmanager.model.Loan;
import io.github.vitorfranca089.libmanager.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class LoanService {

    private final LoanDAO loanDAO = new LoanDAO();

    public LoanDTO makeLoan(UserDTO userDTO, BookDTO bookDTO) {
        User user = new User(userDTO);
        Book book = new Book(bookDTO);
        Loan loan = new Loan(book, user, LocalDateTime.now(), LocalDateTime.now().plusWeeks(1).withHour(23).withMinute(59));
        return loanDAO.makeLoan(loan);
    }

    public List<LoanDTO> findLoansByUser(UserDTO user){
        return loanDAO.findLoansByUser(user);
    }

    public boolean returnLoan(LoanDTO loanToReturn) {
        return loanDAO.returnLoan(loanToReturn);
    }
}
