package com.example.newlab9.Models;

public class User {

    private String uid;
    private String name, secondName, login, password;
    private String dateOfB;
    private String role; // Добавленное поле для роли пользователя

    public User() {}

    public User(String uid, String name, String secondName, String login, String password, String dateOfB, String role) {
        this.uid = uid;
        this.name = name;
        this.secondName = secondName;
        this.login = login;
        this.password = password;
        this.dateOfB = dateOfB;
        this.role = role;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDateOfB() {
        return dateOfB;
    }

    public void setDateOfB(String dateOfB) {
        this.dateOfB = dateOfB;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
