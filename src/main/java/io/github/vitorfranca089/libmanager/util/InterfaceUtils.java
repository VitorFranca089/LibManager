package io.github.vitorfranca089.libmanager.util;

import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.dto.LoanDTO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;

import java.time.format.DateTimeFormatter;

public class InterfaceUtils {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void printUser(UserDTO userDTO) {
        System.out.println("- Nome: " + userDTO.name());
        System.out.println("- ID do usuário: " + userDTO.id());
        System.out.println("- Nome de usuário: " + userDTO.username());
        System.out.println("- Endereço: "  + userDTO.address());
        System.out.println("- Telefone: " + userDTO.phone());
        System.out.println();
    }

    public static void printBook(BookDTO bookDTO){
        System.out.println("- Livro: " + bookDTO.title());
        System.out.println("- ID livro: " + bookDTO.id());
        System.out.println("- Autor: " + bookDTO.author());
        System.out.println("- Ano de publicação: "  + bookDTO.yearPub());
        System.out.println("- Gênero: " + bookDTO.genre());
        System.out.println("- Disponibilidade: " + (bookDTO.isAvailable() ? "Disponível" : "Emprestado"));
        System.out.println();
    }

    public static void printLoan(LoanDTO loan) {
        System.out.println("- ID empréstimo: " + loan.id());
        System.out.println("- Livro: " + loan.book().title());
        System.out.println("- ID Livro: " + loan.book().id());
        System.out.println("- Data de empréstimo: " + loan.loanDate().format(formatter));
        System.out.println("- Data prevista de devolução: " + loan.dueDate().format(formatter));
        System.out.println("- Status: " + loan.loanStatus());
        System.out.println();
    }

    public static void printBasicUser(UserDTO userDTO){
        System.out.println("- Nome: " + userDTO.name());
        System.out.println("- ID do usuário: " + userDTO.id());
        System.out.println();
    }

}
