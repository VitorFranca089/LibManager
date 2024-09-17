package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.BookDTO;
import io.github.vitorfranca089.libmanager.service.BookService;
import io.github.vitorfranca089.libmanager.util.InputUtils;

public class BookController {

    private final BookService bookService = new BookService();

    public void bookMenu(){
        int op;
        do{
            System.out.println("===== LibManager - Livros =====");
            System.out.println("1 - Adicionar um livro.");
            System.out.println("2 - Editar um livro.");
            System.out.println("3 - Consultar um livro.");
            System.out.println("4 - Remover um livro.");
            System.out.println("5 - Cadastrar usuário.");
            System.out.println("0 - Sair.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> addBook();
                case 2 -> updateBook();
            }
        }while(op != 0);
    }

    private void addBook(){
        char op;
        System.out.println("===== LibManager - Registro de livro =====");
        String name = InputUtils.getStringNotBlank("Digite o nome do livro:");
        String author = InputUtils.getStringNotBlank("Digite o nome do autor do livro:");
        int pubYear = InputUtils.getIntYear("Digite o ano de publicação do livro:");
        String genre = InputUtils.getString("Digite o gênero do livro (opcional):");
        do {
            op = InputUtils.getCharOptions("Deseja efetuar o cadastro? (S/N)");
        }while(!(op == 'S' || op == 'N'));
        if(op == 'S'){
            int createdBookId = bookService.addBook(name, author, pubYear, genre);
            System.out.println("Livro cadastrado com o ID " + createdBookId + ".");
            System.out.println();
        }
    }

    private void updateBook(){
        System.out.println("===== LibManager - Editar livro =====");
        int bookId = InputUtils.getInt("Digite o ID do livro a ser alterado:");
        BookDTO foundBook = bookService.findBookById(bookId);
        System.out.println();
        if(foundBook != null){
            printBook(foundBook);
            char op;
            do{
                System.out.println("= Edição de Livro =");
                System.out.println("OBS: Deixe em branco o campo que não queira mudar");
                String newTitle = InputUtils.getString("Digite o novo nome:");
                String newAuthor = InputUtils.getString("Digite o novo nome do autor do livro:");
                int newYearPub = InputUtils.getIntYear("Digite o novo ano de publicação do livro:");
                String newGenre = InputUtils.getString("Digite o novo gênero do livro:");
                op = InputUtils.getCharOptions("Deseja efetuar a alteração? (S/N)");

                if(op == 'S')
                    if(bookService.updateBook(foundBook, newTitle, newAuthor, newYearPub, newGenre))
                        System.out.println("Livro alterado com sucesso!");
                    else
                        System.out.println("Erro na alteração.");
            }while(!(op == 'S' || op == 'N'));
            System.out.println();
        }else{
            System.out.println("Livro não cadastrado.");
            System.out.println();
        }
    }

    private void printBook(BookDTO bookDTO){
        System.out.println("- Livro: " + bookDTO.title());
        System.out.println("- ID livro: " + bookDTO.id());
        System.out.println("- Autor: " + bookDTO.author());
        System.out.println("- Ano de publicação: "  + bookDTO.yearPub());
        System.out.println("- Gênero: " + bookDTO.genre());
        System.out.println("- Disponibilidade: " + (bookDTO.isAvailable() ? "Disponível" : "Emprestado"));
        System.out.println();
    }

}
