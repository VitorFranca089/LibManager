package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.enums.Role;
import io.github.vitorfranca089.libmanager.service.UserService;
import io.github.vitorfranca089.libmanager.util.InputUtils;
import io.github.vitorfranca089.libmanager.util.InterfaceUtils;

public class UserController {

    private final UserService userService = new UserService();

    public void libUserMenu(){
        int op;
        do {
            System.out.println("===== LibManager - Gerenciamento de usuários =====");
            System.out.println("1 - Ver informações de usuário.");
            System.out.println("2 - Cadastrar usuário.");
            System.out.println("3 - Editar informações de usuário.");
            System.out.println("4 - Tornar senha padrão.");
            System.out.println("0 - Sair.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> libQueryUserMenu();
                case 2 -> libUserSingUp();
                case 3 -> libUpdateUser();
                case 4 -> libToDefaultPasswordUser();
            }
        }while(op != 0);
    }

    private void libQueryUserMenu() {
        int op;
        do{
            System.out.println("===== Consulta de usuários =====");
            System.out.println("1 - Consultar usuário por ID.");
            System.out.println("0 - Voltar.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> getUserById();
            }
        }while(op != 0);
    }

    private void libUserSingUp() {
        System.out.println("===== LibManager - Cadastro de Usuário =====");
        String name = InputUtils.getStringNotBlank("Digite seu nome:");
        String address = InputUtils.getStringNotBlank("Digite seu endereço:");
        String phone = InputUtils.getStringNotBlank("Digite seu telefone:");
        Role role = InputUtils.getCharOptions("Tipo de usuário (B/U):") == 'B' ? Role.LIBRARIAN : Role.COMMON;
        UserDTO response = userService.singUp(name, UserService.DEFAULT_PASS, address, phone, role);
        System.out.println();
        System.out.println("===== Usuário criado =====");
        System.out.println("ID: " + response.id());
        System.out.println("Nome de usuário: " + response.username());
        System.out.println("OBS: Altere a senha padrão " + UserService.DEFAULT_PASS + " na página do seu perfil.");
        System.out.println();
    }

    private void libUpdateUser(){
        System.out.println("===== LibManager - Editar Usuário =====");
        int userId = InputUtils.getInt("Digite o ID do usuário a ser alterado:");
        UserDTO foundUser = userService.findUserById(userId);
        System.out.println();
        if(foundUser != null){
            InterfaceUtils.printUser(foundUser);
            char op;
            do{
                System.out.println("= Edição de Usuário =");
                System.out.println("OBS: Deixe em branco o campo que não queira mudar");
                String newName = InputUtils.getString("Digite o novo nome:");
                String newAddress = InputUtils.getString("Digite o novo endereço:");
                String newPhone = InputUtils.getString("Digite o novo telefone:");
                op = InputUtils.getCharOptions("Deseja efetuar a alteração? (S/N)");

                if(op == 'S')
                    if(userService.updateUser(foundUser, newName, newAddress, newPhone))
                        System.out.println("Usuário alterado com sucesso!");
                    else
                        System.out.println("Erro na alteração.");
            }while(!(op == 'S' || op == 'N'));
            System.out.println();
        }else{
            System.out.println("Usuário não cadastrado.");
            System.out.println();
        }
    }

    private void libToDefaultPasswordUser() {
        System.out.println("===== LibManager - Tornar senha padrão =====");
        int userId = InputUtils.getInt("Digite o ID do usuário:");
        UserDTO foundUser = userService.findUserById(userId);
        if(foundUser != null){
            InterfaceUtils.printUser(foundUser);
            char op = InputUtils.getCharOptions("Você deseja tornar a senha deste usuário para a senha padrão? (S/N)");
            if(op == 'S')
                if(userService.toDefaultPassword(foundUser.id()))
                    System.out.println("Senha alterada com sucesso.");
                else
                    System.out.println("Erro na alteração.");
            System.out.println();
        }else{
            System.out.println("Usuário não encontrado.");
            System.out.println();
        }
    }

    private void getUserById() {
        System.out.println("===== LibManager - Consultar usuário =====");
        int userId = InputUtils.getInt("Digite o ID do usuário:");
        UserDTO foundUser = userService.findUserById(userId);
        System.out.println();
        if(foundUser != null){
            InterfaceUtils.printUser(foundUser);
            InputUtils.getString("Digite alguma tecla para voltar ao menu...");
        }else{
            System.out.println("Usuário não encontrado.");
            System.out.println();
        }
    }

    public void singUp(){
        System.out.println("===== LibManager =====");
        String name = InputUtils.getStringNotBlank("Digite seu nome:");
        String password = InputUtils.getStringNotBlank("Digite sua senha:");
        String address = InputUtils.getStringNotBlank("Digite seu endereço:");
        String phone = InputUtils.getStringNotBlank("Digite seu telefone:");
        UserDTO response = userService.singUp(name, password, address, phone, Role.COMMON);
        System.out.println();
        System.out.println("===== Usuário criado =====");
        System.out.println("ID: " + response.id());
        System.out.println("Nome de usuário: " + response.username());
        System.out.println();
    }

    public UserDTO login(){
        System.out.println("===== LibManager =====");
        String name = InputUtils.getStringNotBlank("Digite seu nome:");
        String password = InputUtils.getStringNotBlank("Digite sua senha:");
        System.out.println();
        return userService.login(name, password);
    }

}
