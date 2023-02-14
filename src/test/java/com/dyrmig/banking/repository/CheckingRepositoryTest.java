package com.dyrmig.banking.repository;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Checking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CheckingRepositoryTest {
    @Autowired
    CheckingRepository checkingRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    private AccountHolder accountHolder1;
    private Checking checking1;
    @BeforeEach
    void setUp() {
        Address address1 = new Address("Calle 13", "13","Barcelona", "Barcelona", "91234", "Spain");
        accountHolder1 = new AccountHolder("Dmitri", LocalDate.of(1986, 10, 26),address1);
        accountHolderRepository.save(accountHolder1);

        checking1 = new Checking(new Money(new BigDecimal("60000")), "chcktsd82h8e", accountHolder1);
        checkingRepository.save(checking1);

        accountHolder1.setOwnedAccountList(List.of(checking1));
        accountHolderRepository.save(accountHolder1);
    }

    @AfterEach
    void tearDown() {
        accountHolder1.setOwnedAccountList(null);
        checkingRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void saveNewCheckingAccount(){
        Optional<Checking> checkingOptional = checkingRepository.findById(checking1.getId());
        assertTrue(checkingOptional.isPresent());
        assertEquals("chcktsd82h8e", checkingOptional.get().getSecretKey());
    }

    @Test
    void setBalanceBelowMinimumBalance(){
        Optional<Checking> checkingOptional = checkingRepository.findById(checking1.getId());
        checkingOptional.get().setBalance(new Money(new BigDecimal("249")));
        assertEquals(new BigDecimal("209.00"), checkingOptional.get().getBalance().getAmount());
    }
    @Test
    void accountHolderWithAccounts(){
        Optional<AccountHolder> accountHolderOptional = accountHolderRepository.accountHolderWithAccounts(accountHolder1.getId());
        assertTrue(accountHolderOptional.isPresent());
        assertEquals("chcktsd82h8e", accountHolderOptional.get().getOwnedAccountList().get(0).getSecretKey());
    }
}