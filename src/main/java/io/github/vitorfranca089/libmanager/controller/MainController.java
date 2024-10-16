package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.enums.Role;
import io.github.vitorfranca089.libmanager.util.InputUtils;

public class MainController {

    private final UserController userController = new UserController();
    private final BookController bookController = new BookController();
    private final LoanController loanController = new LoanController();
    private final ProfileController profileController = new ProfileController();

    public void startMenu(){
        int op;
        do{
            System.out.println("===== LibManager =====");
            System.out.println("1 - Efetuar login.");
            System.out.println("2 - Efetuar cadastro.");
            System.out.println("0 - Sair.");
            op = InputUtils.getInt();
            System.out.println();
            switch (op){
                case 1 -> {
                    UserDTO loggedUser = userController.login();
                    if(loggedUser != null) userMenu(loggedUser);
                }
                case 2 -> userController.singUp();
            }
        }while(op != 0);

    }

    private void userMenu(UserDTO user){
        if(user.role().equals(Role.LIBRARIAN)) librarianMenu(user);
        else commonUserMenu(user);
    }

    private void librarianMenu(UserDTO user){
        int op;
        do {
            System.out.println("===== LibManager - "+ user.username() +" =====");
            System.out.println("1 - Livros.");
            System.out.println("2 - Usuários.");
            System.out.println("3 - Empréstimos.") ;
            System.out.println("4 - Perfil.");
            System.out.println("0 - Logout.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> bookController.bookMenu();
                case 2 -> userController.libUserMenu();
                case 3 -> loanController.libLoanMenu();
                case 4 -> profileController.profileMenu(user);
            }
            System.out.println();
        }while(op != 0);
    }

    private void commonUserMenu(UserDTO user){
        int op;
        do {
            System.out.println("===== LibManager - "+ user.username() +" =====");
            System.out.println("1 - Empréstimos.") ;
            System.out.println("2 - Perfil.");
            System.out.println("0 - Logout.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> loanController.userLoanMenu(user);
                case 2 -> profileController.profileMenu(user);
            }
            System.out.println();
        }while(op != 0);
    }

}
