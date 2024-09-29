package io.github.vitorfranca089.libmanager.service;

import io.github.vitorfranca089.libmanager.dao.AuthDAO;
import io.github.vitorfranca089.libmanager.dao.UserDAO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.User;
import io.github.vitorfranca089.libmanager.model.enums.Role;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    public UserDTO singUp(String name, String password, String address, String phone, Role role){
        User user = new User(name, password, address, phone, role);
        return userDAO.singUpUser(user);
    }

    public UserDTO login(String username, String password){
        return authDAO.login(username, password);
    }

    public UserDTO findUserById(int userId) {
        return userDAO.findUserByID(userId);
    }
}
