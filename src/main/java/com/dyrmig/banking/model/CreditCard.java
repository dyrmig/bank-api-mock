package com.dyrmig.banking.model;

import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.enums.Status;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class CreditCard extends Account{
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "credit_limit_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "credit_limit_currency"))
    })
    private Money creditLimit;
    @Column(columnDefinition="DECIMAL(19,4)")
    private BigDecimal interestRate;
    private LocalDate lastInterestAdded = super.getCreationDate();

    public CreditCard() {
    }
/*    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner) {
        super(balance, secretKey, primaryOwner);
        this.creditLimit = new Money(new BigDecimal("100"), balance.getCurrency());
        this.interestRate = new BigDecimal("0.2");
    }*/
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner, Money creditLimit, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        if(creditLimit == null){
            this.creditLimit = new Money(new BigDecimal("100"), balance.getCurrency());
        } else {setCreditLimit(creditLimit);}
        if(interestRate == null){
            this.interestRate = new BigDecimal("0.2");
        } else {setInterestRate(interestRate);}
    }
/*    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner, Money creditLimit) {
        super(balance, secretKey, primaryOwner);
        setCreditLimit(creditLimit);
        this.interestRate = new BigDecimal("0.2");
    }
    public CreditCard(Money balance, String secretKey, AccountHolder primaryOwner, BigDecimal interestRate) {
        super(balance, secretKey, primaryOwner);
        this.creditLimit = new Money(new BigDecimal("100"), balance.getCurrency());
        setInterestRate(interestRate);
    }*/
    public Money getBalance(){
        long months = ChronoUnit.MONTHS.between(getLastInterestAdded(), LocalDate.now());
        if (months >= 1) {
            // More than a month has passed
            //update the balance with the added interest
            BigDecimal monthlyInterest = interestRate.divide(new BigDecimal("12"));
            super.setBalance(new Money(super.getBalance().getAmount().multiply(monthlyInterest).add(super.getBalance().getAmount())));
            //update the date of the last added interest
            setLastInterestAdded(LocalDate.now());
            return super.getBalance();
        } else {
            return super.getBalance();
        }
    }
    public Money getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Money creditLimit) {
        BigDecimal minValue = new BigDecimal("100");
        BigDecimal maxValue = new BigDecimal("100000");
        if (creditLimit.getAmount().compareTo(minValue) >= 0 && creditLimit.getAmount().compareTo(maxValue) <= 0) {
            this.creditLimit = creditLimit;
        } else if (creditLimit.getAmount().compareTo(minValue) < 0) {
            this.creditLimit = new Money(minValue, creditLimit.getCurrency());
        } else if (creditLimit.getAmount().compareTo(maxValue) > 0) {
            this.creditLimit = new Money(maxValue, creditLimit.getCurrency());
        }
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        BigDecimal minValue = new BigDecimal("0.1");
        BigDecimal maxValue = new BigDecimal("0.2");
        if (interestRate.compareTo(minValue) >= 0 && interestRate.compareTo(maxValue) <= 0) {
            //updating the last date since interestRate changed or was added to the balance only if the new interestRate is different to the previous
            if(interestRate != getInterestRate()){
                setLastInterestAdded(LocalDate.now());
            }
            this.interestRate = interestRate;
        } else if (interestRate.compareTo(minValue) < 0) {
            if(minValue != getInterestRate()){
                setLastInterestAdded(LocalDate.now());
            }
            this.interestRate = minValue;
        } else if (interestRate.compareTo(maxValue) > 0) {
            if(maxValue != getInterestRate()){
                setLastInterestAdded(LocalDate.now());
            }
            this.interestRate = maxValue;
        }
    }

    public LocalDate getLastInterestAdded() {
        return lastInterestAdded;
    }

    public void setLastInterestAdded(LocalDate lastInterestAdded) {
        this.lastInterestAdded = lastInterestAdded;
    }
}
