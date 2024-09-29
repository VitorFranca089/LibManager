package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.BookDTO;
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
                case 1 -> libQueryUserMenu();
                case 2 -> libUserSingUp();
            }
        }while(op != 0);
    }

    private void libQueryUserMenu() {
        // TODO: add all query types of users
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
        final String DEFAULT_PASS = "1234567890";
        System.out.println("===== LibManager - Cadastro de Usuário =====");
        String name = InputUtils.getStringNotBlank("Digite seu nome:");
        String address = InputUtils.getStringNotBlank("Digite seu endereço:");
        String phone = InputUtils.getStringNotBlank("Digite seu telefone:");
        Role role = InputUtils.getCharOptions("Tipo de usuário (B/U):") == 'B' ? Role.LIBRARIAN : Role.COMMON;
        UserDTO response = userService.singUp(name, DEFAULT_PASS, address, phone, role);
        System.out.println();
        System.out.println("===== Usuário criado =====");
        System.out.println("ID: " + response.id());
        System.out.println("Nome de usuário: " + response.username());
        System.out.println("OBS: Altere a senha padrão " + DEFAULT_PASS + " na página do seu perfil.");
        System.out.println();
    }

    private void getUserById() {
        System.out.println("===== LibManager - Consultar usuário =====");
        int userId = InputUtils.getInt("Digite o ID do usuário:");
        UserDTO foundUser = userService.findUserById(userId);
        System.out.println();
        if(foundUser != null){
            printUser(foundUser);
            String waitOp = InputUtils.getString("Digite alguma tecla para voltar ao menu...");
        }else{
            System.out.println("Usuário não encontrado.");
            System.out.println();
        }
    }

    private void printUser(UserDTO userDTO) {
        System.out.println("- Nome: " + userDTO.name());
        System.out.println("- ID do usuário: " + userDTO.id());
        System.out.println("- Nome de usuário: " + userDTO.username());
        System.out.println("- Endereço: "  + userDTO.address());
        System.out.println("- Telefone: " + userDTO.phone());
        System.out.println();
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
