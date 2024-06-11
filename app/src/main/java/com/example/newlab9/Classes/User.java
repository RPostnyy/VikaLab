package com.example.newlab9.Classes;

public class User {
    private String name, surname, date;


    public User(String name, String surname,String date) {
        this.name = name;
        this.surname = surname;
        this.date = date;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
