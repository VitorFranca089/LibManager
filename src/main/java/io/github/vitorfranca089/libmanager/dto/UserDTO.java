package io.github.vitorfranca089.libmanager.dto;

import io.github.vitorfranca089.libmanager.model.User;
import io.github.vitorfranca089.libmanager.model.enums.Role;

public record UserDTO(
        int id,
        String name,
        String username,
        String address,
        String phone,
        Role role
) {
    public UserDTO(User user){
        this(
                user.getId(),
                user.getName(),
                user.getUsername(),
                user.getAddress(),
                user.getPhone(),
                user.getRole()
        );
    }
}
