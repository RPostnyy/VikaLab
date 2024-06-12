package com.example.newlab9.Models;

public class Article {
    private String id;
    private String title;
    private String content;
    private String imageUrl;
    private String creationDate;

    public Article() {
        // Пустой конструктор для Firebase
    }

    public Article(String id, String title, String content, String imageUrl, String creationDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.creationDate = creationDate;
    }

    // Геттеры и сеттеры для полей

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }
}
