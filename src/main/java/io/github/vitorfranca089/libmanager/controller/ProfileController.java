package io.github.vitorfranca089.libmanager.controller;

import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.service.UserService;
import io.github.vitorfranca089.libmanager.util.InputUtils;
import io.github.vitorfranca089.libmanager.util.InterfaceUtils;

public class ProfileController {

    private final UserService userService = new UserService();

    public void profileMenu(UserDTO user) {
        int op;
        do{
            System.out.println("===== LibManager - Perfil " + user.username() + " =====");
            System.out.println("1 - Ver informações de cadastro.");
            System.out.println("2 - Alterar informações de perfil.");
            System.out.println("0 - Sair.");
            op = InputUtils.getInt();
            System.out.println();
            switch(op){
                case 1 -> profileInfo(user.id());
                case 2 -> profileUpdate(user.id());
            }
        }while(op != 0);
    }

    private void profileInfo(int userId) {
        System.out.println("===== Perfil =====");
        InterfaceUtils.printUser(userService.findUserById(userId));
    }

    private void profileUpdate(int userId) {
        System.out.println("===== Editar informações =====");
        UserDTO foundUser = userService.findUserById(userId);
        InterfaceUtils.printUser(foundUser);
        String currentPass = InputUtils.getString("Digite sua senha para alterar suas informações:");
        if(userService.verifyPassword(currentPass, foundUser.id())){
            char op;
            do{
                System.out.println("= Edição de Usuário =");
                System.out.println("OBS: Deixe em branco o campo que não queira mudar");
                String newName = InputUtils.getString("Digite o novo nome:");
                String newPass = InputUtils.getString("Digite o nova senha:");
                String newAddress = InputUtils.getString("Digite o novo endereço:");
                String newPhone = InputUtils.getString("Digite o novo telefone:");
                op = InputUtils.getCharOptions("Deseja efetuar a alteração? (S/N)");

                if(op == 'S')
                    if(userService.updateUser(foundUser, newName, newPass, newAddress, newPhone))
                        System.out.println("Alterado com sucesso!");
                    else
                        System.out.println("Erro na alteração.");
            }while(!(op == 'S' || op == 'N'));
        }else
            System.out.println("Senha incorreta, tente novamente...");
        System.out.println();
    }

}
