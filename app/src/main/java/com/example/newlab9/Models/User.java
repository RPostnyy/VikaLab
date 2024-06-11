package com.example.newlab9.Models;

public class User {

    private String name, secondName, login, password;
    private String dateOfB;

    public User(){}

    public User(String name, String secondName, String login, String password, String dateOfB) {
        this.name = name;
        this.secondName = secondName;
        this.login = login;
        this.password = password;
        this.dateOfB = dateOfB;
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

}
