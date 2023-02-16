package com.dyrmig.banking.repository;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.model.AccountHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class AccountHolderRepositoryTest {
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private AccountHolder accountHolder1;

    @BeforeEach
    void setUp() {
        Address address1 = new Address("Calle 13", "13","Barcelona", "Barcelona", "91234", "Spain");
        accountHolder1 = new AccountHolder("Dmitri", "dmitri123", passwordEncoder.encode("passord1234"), LocalDate.of(1986, 10, 26),address1);
        accountHolderRepository.save(accountHolder1);
    }

    @AfterEach
    void tearDown() {
        accountHolderRepository.deleteAll();
    }

    @Test
    void saveNewAccountHolder(){
        Optional<AccountHolder> accountHolderOptional = accountHolderRepository.findById(accountHolder1.getId());
        assertTrue(accountHolderOptional.isPresent());
        assertEquals("Dmitri", accountHolderOptional.get().getName());
    }
}