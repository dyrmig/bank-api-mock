package com.dyrmig.banking.model;

import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.enums.Status;
import jakarta.persistence.Entity;

@Entity
public class StudentChecking extends Account{
    public StudentChecking() {
    }
    public StudentChecking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
    }
}
