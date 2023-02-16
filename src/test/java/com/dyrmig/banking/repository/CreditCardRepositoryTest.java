package com.dyrmig.banking.repository;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.CreditCard;
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
class CreditCardRepositoryTest {
    @Autowired
    CreditCardRepository creditCardRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    private AccountHolder accountHolder1;
    private CreditCard creditCard1;
    @BeforeEach
    void setUp() {
        Address address1 = new Address("Calle 13", "13","Barcelona", "Barcelona", "91234", "Spain");
        accountHolder1 = new AccountHolder("Dmitri", "dmitri123", "pasword123", LocalDate.of(1986, 10, 26),address1);
        accountHolderRepository.save(accountHolder1);

        //creditCard1 = new CreditCard(new Money(new BigDecimal("60000")), "cdtsd82h8e", accountHolder1, new Money(new BigDecimal("60")));
        creditCard1 = new CreditCard(new Money(new BigDecimal("60000")), "cdtsd82h8e", accountHolder1, new Money(new BigDecimal("99")));
        creditCardRepository.save(creditCard1);
    }

    @AfterEach
    void tearDown() {
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
    }

    @Test
    void saveNewCreditCardAccount(){
        Optional<CreditCard> optionalCreditCard = creditCardRepository.findById(creditCard1.getId());
        assertTrue(optionalCreditCard.isPresent());
        assertEquals("cdtsd82h8e", optionalCreditCard.get().getSecretKey());
        assertEquals(new BigDecimal("0.2000"), optionalCreditCard.get().getInterestRate());
        assertEquals(new BigDecimal("100.00"), optionalCreditCard.get().getCreditLimit().getAmount());
        assertEquals(LocalDate.of(2023, 02,14) , optionalCreditCard.get().getCreationDate());
    }
}