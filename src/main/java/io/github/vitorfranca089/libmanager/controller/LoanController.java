package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.dto.LoanDTO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.service.BookService;
import io.github.vitorfranca089.libmanager.service.LoanService;
import io.github.vitorfranca089.libmanager.service.UserService;
import io.github.vitorfranca089.libmanager.util.InputUtils;
import io.github.vitorfranca089.libmanager.util.InterfaceUtils;

import java.util.List;
import java.util.Optional;

public class LoanController {

    private final LoanService loanService = new LoanService();
    private final UserService userService = new UserService();
    private final BookService bookService = new BookService();

    public void libLoanMenu(){
        int op;
        do {
            System.out.println("===== LibManager - Empréstimo de livros =====");
            System.out.println("1 - Fazer empréstimo de livro.");
            System.out.println("2 - Fazer devolução de livro.");
            System.out.println("3 - Consultar empréstimos por usuário.");
            System.out.println("0 - Sair.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> libLoanRegister();
                case 2 -> libLoanReturn();
                case 3 -> libLoanQuery();
            }
        }while(op != 0);
    }

    private void libLoanRegister() {
        System.out.println("===== LibManager - Fazer empréstimo =====");
        int userId = InputUtils.getInt("Digite o ID do usuário:");
        UserDTO foundUser = userService.findUserById(userId);
        if(foundUser != null){
            InterfaceUtils.printBasicUser(foundUser);
            int bookId = InputUtils.getInt("Digite o ID do livro a ser emprestado:");
            BookDTO foundBook = bookService.findBookById(bookId);
            if(foundBook.isAvailable()){
                InterfaceUtils.printBook(foundBook);
                char op = InputUtils.getCharOptions("Deseja realizar o empréstimo do livro? (S/N)");
                if(op == 'S'){
                    LoanDTO loan = loanService.makeLoan(foundUser, foundBook);
                    if(loan != null) {
                        InterfaceUtils.printLoan(loan);
                        InputUtils.getString("Digite alguma tecla para voltar ao menu...");
                    }else
                        System.out.println("Erro no empréstimo do livro.");
                }
            }else
                System.out.println("Livro já está emprestado.");
        }else
            System.out.println("Usuário não encontrado.");
        System.out.println();
    }

    private void libLoanReturn() {
        System.out.println("===== LibManager - Devolução de livro =====");
        int userId = InputUtils.getInt("Digite o ID do usuário:");
        UserDTO foundUser = userService.findUserById(userId);
        if(foundUser != null){
            List<LoanDTO> userLoans = loanService.findLoansByUser(foundUser);
            if(!(userLoans.isEmpty())){
                System.out.println();
                userLoans.forEach(InterfaceUtils::printLoan);

                int loanToReturn = InputUtils.getInt("Digite o ID do empréstimo do livro a ser devolvido:");
                Optional<LoanDTO> loanToReturnOpt = userLoans.stream()
                        .filter(loan -> loan.id() == loanToReturn)
                        .findFirst();

                if (loanToReturnOpt.isPresent()) {
                    if (loanService.returnLoan(loanToReturnOpt.get()))
                        System.out.println("Livro devolvido com sucesso.");
                    else
                        System.out.println("Erro ao devolver livro.");
                }else
                    System.out.println("Livro não está emprestado pelo usuário.");
            }else
                System.out.println("O usuário não fez empréstimo de livros.");
        }else
            System.out.println("Usuário não encontrado.");
        System.out.println();
    }

    private void libLoanQuery() {
        System.out.println("===== LibManager - Consulta de empréstimos =====");
        int userId = InputUtils.getInt("Digite o ID do usuário:");
        UserDTO foundUser = userService.findUserById(userId);
        if(foundUser != null){
            List<LoanDTO> userLoans = loanService.findLoansByUser(foundUser);
            if(!(userLoans.isEmpty())){
                System.out.println();
                userLoans.forEach(InterfaceUtils::printLoan);
                InputUtils.getString("Digite alguma tecla para voltar ao menu...");
            }else
                System.out.println("O usuário não fez empréstimo de livros.");
        }else
            System.out.println("Usuário não encontrado.");
        System.out.println();
    }

}
