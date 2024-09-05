package io.github.vitorfranca089.libmanager.model.enums;

public enum Role {

    COMMON("common"),
    LIBRARIAN("librarian");

    private String role;

    Role(String role){
        this.role = role;
    }

    public String getRole() {
        return role;
    }

}
