package io.github.vitorfranca089.libmanager.util;

import io.github.vitorfranca089.libmanager.model.enums.Role;

public class CredencialUtils {

    public static String makeUsername(String name, Role role, int id){
        String firstName = name.split(" ")[0];
        String roleType = role.getRole().equals("librarian") ? "L" : "C";
        return firstName + roleType + id;
    }

}
