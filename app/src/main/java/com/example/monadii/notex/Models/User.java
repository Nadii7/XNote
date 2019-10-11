package com.example.monadii.notex.Models;

public class User {
   private String name, email, search, uid, avatar, cover;

    public User() {
    }

    public User(String name, String email, String search, String uid, String avatar, String cover) {
        this.name = name;
        this.email = email;
        this.search = search;
        this.uid = uid;
        this.avatar = avatar;
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}