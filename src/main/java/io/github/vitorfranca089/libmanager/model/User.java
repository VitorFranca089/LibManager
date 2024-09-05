package io.github.vitorfranca089.libmanager.model;

import io.github.vitorfranca089.libmanager.model.enums.Role;

public class User {

    private int id;
    private String name;
    private String username;
    private String password;
    private String address;
    private String phone;
    private Role role;

    public User() {
    }

    public User(String name, String username, String password, String address, String phone, Role role) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

}
