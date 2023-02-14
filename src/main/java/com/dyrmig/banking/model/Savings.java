package com.dyrmig.banking.model;

import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.enums.Status;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Savings extends Account{
    @Column(columnDefinition="DECIMAL(19,4)")
    private BigDecimal interestRate;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "minimum_balance_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "minimum_balance_currency"))
    })
    private Money minimumBalance;
    private LocalDate lastInterestAdded = super.getCreationDate();

    public Savings() {
    }

    public Savings(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        this.interestRate = new BigDecimal("0.0025");
        this.minimumBalance = new Money(new BigDecimal("1000.00"), balance.getCurrency());
    }

    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, BigDecimal interestRate, Money minimumBalance) {
        super(balance, secretKey, primaryOwner);
        setInterestRate(interestRate);
        setMinimumBalance(minimumBalance);
    }
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        setInterestRate(interestRate);
        this.minimumBalance = new Money(new BigDecimal("1000.00"), balance.getCurrency());
    }
    public Savings(Money balance, String secretKey, AccountHolder primaryOwner, Money minimumBalance) {
        super(balance, secretKey, primaryOwner);
        this.interestRate = new BigDecimal("0.0025");
        setMinimumBalance(minimumBalance);
    }
    public void setBalance(Money balance) {
        if(balance.getAmount().compareTo(minimumBalance.getAmount()) < 0){
            super.setBalance(new Money(balance.getAmount().subtract(super.getPenaltyFee().getAmount()), super.getBalance().getCurrency()));
        } else {
            super.setBalance(new Money(balance.getAmount(), super.getBalance().getCurrency()));
        }
    }
    public Money getBalance(){
        long years = ChronoUnit.YEARS.between(getLastInterestAdded(), LocalDate.now());
        if (years >= 1) {
            // More than a year has passed
            //update the balance with the added interest
            setBalance(new Money(super.getBalance().getAmount().multiply(getInterestRate()).add(super.getBalance().getAmount())));
            //update the date of the last added interest
            setLastInterestAdded(LocalDate.now());
            return super.getBalance();
        } else {
            return super.getBalance();
        }
    }
    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        if (interestRate.compareTo(new BigDecimal("0.5")) > 0) {
            //System.out.println("The number is greater than 0.5");
            this.interestRate = new BigDecimal("0.5");
        } else {
            //System.out.println("The number is less than or equal to 0.5");
            this.interestRate = interestRate;
        }
    }

    public Money getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(Money minimumBalance) {
        if (minimumBalance.getAmount().compareTo(new BigDecimal("100.00")) > 0) {
            this.minimumBalance = minimumBalance;
        } else {
            this.minimumBalance = new Money(new BigDecimal("100.00"), minimumBalance.getCurrency());
        }
    }

    public LocalDate getLastInterestAdded() {
        return lastInterestAdded;
    }

    public void setLastInterestAdded(LocalDate lastInterestAdded) {
        this.lastInterestAdded = lastInterestAdded;
    }
}
