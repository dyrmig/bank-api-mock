package com.dyrmig.banking.repository;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.enums.Role;
import com.dyrmig.banking.enums.Status;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Savings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class SavingsRepositoryTest {
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    private AccountHolder accountHolder1;
    private Savings savingsAccount1;
    @BeforeEach
    void setUp() {
        Address address1 = new Address("Calle 13", "13","Barcelona", "Barcelona", "91234", "Spain");
        accountHolder1 = new AccountHolder("Dmitri", LocalDate.of(1986, 10, 26),address1);
        accountHolderRepository.save(accountHolder1);

        //savingsAccount1 = new Savings(new Money(new BigDecimal("60000")), "uasd82h8e", accountHolder1, new Money(new BigDecimal("60")));
        savingsAccount1 = new Savings(new Money(new BigDecimal("60000")), "uasd82h8e", accountHolder1, new BigDecimal("0.66"), new Money(new BigDecimal("99")));
        savingsRepository.save(savingsAccount1);


    }

    @AfterEach
    void tearDown() {
        savingsRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }
    @Test
    void saveNewSavingsAccount(){
        Optional<Savings> optionalSavings = savingsRepository.findById(savingsAccount1.getId());
        assertTrue(optionalSavings.isPresent());
        assertEquals("uasd82h8e", optionalSavings.get().getSecretKey());
        assertEquals(new BigDecimal("60000.00"), optionalSavings.get().getBalance().getAmount());
        assertEquals(new BigDecimal("0.5000"), optionalSavings.get().getInterestRate());
        assertEquals(new BigDecimal("100.00"), optionalSavings.get().getMinimumBalance().getAmount());
    }
}