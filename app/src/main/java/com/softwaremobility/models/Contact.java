package com.softwaremobility.models;

/**
 * Created by darkgeat on 3/12/17.
 */

public class Contact {
    private int id;
    private String name;
    private String photo_path;
    private String group;
    private String phone;
    private String email;
    private String typePhone;

    public Contact(int id, String name, String photo_path, String group, String phone, String email, String typePhone) {
        this.id = id;
        this.name = name;
        this.photo_path = photo_path;
        this.group = group;
        this.phone = phone;
        this.email = email;
        this.typePhone = typePhone;
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

    public String getPhoto_path() {
        return photo_path;
    }

    public void setPhoto_path(String photo_path) {
        this.photo_path = photo_path;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTypePhone() {
        return typePhone;
    }

    public void setTypePhone(String typePhone) {
        this.typePhone = typePhone;
    }
}
