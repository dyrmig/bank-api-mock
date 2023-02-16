package com.dyrmig.banking.model;

import jakarta.persistence.Entity;

@Entity
public class Admin extends User{
    public Admin() {
    }

    public Admin(String name, String username, String password) {
        super(name, username, password);
    }
}
