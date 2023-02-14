package com.dyrmig.banking.model;

import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.enums.Status;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Checking extends Account {
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "minimum_balance_currency"))
    })
    private Money minimumBalance;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "maintenance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "maintenance_currency"))
    })
    private Money monthlyMaintenanceFee;

    public Checking() {
    }

    public Checking(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        this.minimumBalance = new Money(new BigDecimal("250"), balance.getCurrency());
        this.monthlyMaintenanceFee = new Money(new BigDecimal("12"), balance.getCurrency());
    }
    public void setBalance(Money balance) {
        if(balance.getAmount().compareTo(minimumBalance.getAmount()) < 0){
            super.setBalance(new Money(balance.getAmount().subtract(super.getPenaltyFee().getAmount()), super.getBalance().getCurrency()));
        } else {
            super.setBalance(new Money(balance.getAmount(), super.getBalance().getCurrency()));
        }
    }
    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public Money getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(Money monthlyMaintenanceFee) {
        this.monthlyMaintenanceFee = monthlyMaintenanceFee;
    }
}
