package com.dyrmig.banking.model;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class AccountHolder extends User{
    private LocalDate dateOfBirth;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetName", column = @Column(name = "street")),
            @AttributeOverride(name = "streetNumber", column = @Column(name = "street_number")),
            @AttributeOverride(name = "city", column = @Column(name = "city")),
            @AttributeOverride(name = "province", column = @Column(name = "province")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "country"))
    })
    private Address primaryAddress;
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "streetName", column = @Column(name = "mailing_street")),
            @AttributeOverride(name = "streetNumber", column = @Column(name = "mailing_street_number")),
            @AttributeOverride(name = "city", column = @Column(name = "mailing_city")),
            @AttributeOverride(name = "province", column = @Column(name = "mailing_province")),
            @AttributeOverride(name = "postalCode", column = @Column(name = "mailing_postal_code")),
            @AttributeOverride(name = "country", column = @Column(name = "mailing_country"))
    })
    private Address mailingAddress;
    @OneToMany(mappedBy = "primaryOwner")
    private List<Account> ownedAccountList;
    @OneToMany(mappedBy = "secondaryOwner")
    private List<Account> secondaryOwnedAccountList;

    public AccountHolder() {
    }

    public AccountHolder(String name, LocalDate dateOfBirth, Address primaryAddress) {
        super(name, Role.USER);
        this.dateOfBirth = dateOfBirth;
        this.primaryAddress = primaryAddress;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Address getPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(Address primaryAddress) {
        this.primaryAddress = primaryAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public List<Account> getOwnedAccountList() {
        return ownedAccountList;
    }

    public void setOwnedAccountList(List<Account> ownedAccountList) {
        this.ownedAccountList = ownedAccountList;
    }

    public List<Account> getSecondaryOwnedAccountList() {
        return secondaryOwnedAccountList;
    }

    public void setSecondaryOwnedAccountList(List<Account> secondaryOwnedAccountList) {
        this.secondaryOwnedAccountList = secondaryOwnedAccountList;
    }
}
