package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.enums.Role;
import io.github.vitorfranca089.libmanager.service.UserService;
import io.github.vitorfranca089.libmanager.util.InputUtils;

public class UserController {

    private final UserService userService = new UserService();

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
