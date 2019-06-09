package com.dev.aman.android_contact_app.model;

import java.io.Serializable;

public class ContactModel implements Serializable {
    private String name;
    private String email;
    private String number;
    private String image;

    public ContactModel() {
    }

    public ContactModel(String name, String email, String number, String image) {
        this.name = name;
        this.email = email;
        this.number = number;
        this.image = image;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
