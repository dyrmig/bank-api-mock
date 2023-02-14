package com.dyrmig.banking.model;

import com.dyrmig.banking.enums.Role;
import jakarta.persistence.Entity;

@Entity
public class Admin extends User{
    public Admin() {
    }

    public Admin(String name, Role role) {
        super(name, role);
    }
}
