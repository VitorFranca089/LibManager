package io.github.vitorfranca089.libmanager.service;

import io.github.vitorfranca089.libmanager.dao.AuthDAO;
import io.github.vitorfranca089.libmanager.dao.UserDAO;
import io.github.vitorfranca089.libmanager.dto.UserDTO;
import io.github.vitorfranca089.libmanager.model.User;
import io.github.vitorfranca089.libmanager.model.enums.Role;
import io.github.vitorfranca089.libmanager.util.UpdateUtils;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final AuthDAO authDAO = new AuthDAO();

    public static final String DEFAULT_PASS = "1234567890";

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

    public boolean updateUser(UserDTO user, String newName, String newAddress, String newPhone) {
        User userToUpdate = new User(user);
        UpdateUtils.updateIfNotEmpty(newName, userToUpdate::setName);
        UpdateUtils.updateIfNotEmpty(newAddress, userToUpdate::setAddress);
        UpdateUtils.updateIfNotEmpty(newPhone, userToUpdate::setPhone);
        return userDAO.updateUser(userToUpdate);
    }

    public boolean updateUser(UserDTO user, String newName, String newPassword, String newAddress, String newPhone) {
        User userToUpdate = new User(user);
        UpdateUtils.updateIfNotEmpty(newName, userToUpdate::setName);
        UpdateUtils.updateIfNotEmpty(newPassword, userToUpdate::setPassword);
        UpdateUtils.updateIfNotEmpty(newAddress, userToUpdate::setAddress);
        UpdateUtils.updateIfNotEmpty(newPhone, userToUpdate::setPhone);
        if(userToUpdate.getPassword() == null)
            return userDAO.updateUser(userToUpdate);
        return userDAO.updateUserWithPass(userToUpdate);
    }

    public boolean toDefaultPassword(int id) {
        return userDAO.toDefaultPassword(id, DEFAULT_PASS);
    }

    public boolean verifyPassword(String oldPass, int userId) {
        return userDAO.verifyPassword(oldPass, userId);
    }
}
