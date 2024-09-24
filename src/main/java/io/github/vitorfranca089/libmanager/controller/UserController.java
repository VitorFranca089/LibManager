package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.enums.Role;
import io.github.vitorfranca089.libmanager.service.UserService;
import io.github.vitorfranca089.libmanager.util.InputUtils;

public class UserController {

    private final UserService userService = new UserService();

    public void libUserMenu(){
        int op;
        do {
            System.out.println("===== LibManager - Gerenciamento de usuários =====");
            System.out.println("1 - Ver informações de usuário.");
            System.out.println("2 - Cadastrar usuário.");
            System.out.println("3 - Editar informações de usuário.");
            System.out.println("0 - Sair.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> queryUserMenu();
            }
        }while(op != 0);
    }

    private void queryUserMenu() {
        // TODO: add all query types of users
        int op;
        do{
            System.out.println("===== Consulta de usuários =====");
            System.out.println("1 - Consultar usuário por ID.");
            System.out.println("0 - Voltar.");
            op = InputUtils.getInt();
            System.out.println();
        }while(op != 0);
    }

    public void singUp(){
        System.out.println("===== LibManager =====");
        String name = InputUtils.getStringNotBlank("Digite seu nome:");
        String password = InputUtils.getStringNotBlank("Digite sua senha:");
        String address = InputUtils.getStringNotBlank("Digite seu endereço:");
        String phone = InputUtils.getStringNotBlank("Digite seu telefone");
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
