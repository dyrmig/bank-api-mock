package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Checking;
import com.dyrmig.banking.model.ThirdParty;
import com.dyrmig.banking.repository.AccountHolderRepository;
import com.dyrmig.banking.repository.CheckingRepository;
import com.dyrmig.banking.repository.ThirdPartyRepository;
import com.dyrmig.banking.service.interfaces.ThirdPartyService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ThirdPartyControllerImplTest {
    @Autowired
    private ThirdPartyRepository thirdPartyRepository;
    @Autowired
    private ThirdPartyService thirdPartyService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private WebApplicationContext webApplicationContext; //simula un server
    private MockMvc mockMvc; //simula peticiones HTTP
    private final ObjectMapper objectMapper = new ObjectMapper(); //construir objetos json de las clases de java

    private ThirdParty thirdParty1;
    private ThirdParty thirdParty2;
    private AccountHolder accountHolder1;
    private Checking checking1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        thirdParty1 = new ThirdParty("MasterCard");
        thirdParty2 = new ThirdParty("Visa");
        thirdPartyRepository.saveAll(List.of(thirdParty1, thirdParty2));

        accountHolder1 = new AccountHolder(
                "Lolo Perez",
                "loloperez",
                "pasword1234",
                LocalDate.of(1986, 10, 26),
                new Address("Calle 13", "13","Barcelona", "Barcelona", "91234", "Spain")
        );
        accountHolderRepository.save(accountHolder1);

        checking1 = new Checking(new Money(new BigDecimal("60000")), "chcktsd82h8e", accountHolder1);
        checkingRepository.save(checking1);

        accountHolder1.setOwnedAccountList(List.of(checking1));
        accountHolderRepository.save(accountHolder1);
    }

    @AfterEach
    void tearDown() {
    }
    @Test
    void findAll() throws Exception {
        MvcResult mvcResult = mockMvc
                .perform(get("/thirdparty")) //hicimos un static import de MockMvcRequestBuilders
                .andExpect(status().isOk()) //importamos: import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //importamos: import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
                .andReturn();
        //System.out.println(mvcResult.getResponse().getContentAsString().contains("Intro to Java"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("MasterCard"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Visa"));
    }
    @Test
    void saveThirdParty() throws Exception {
        ThirdParty newThirdParty = new ThirdParty("American Express");
        String body = objectMapper.writeValueAsString(newThirdParty); //convertimos el objeto java a un string json

        MvcResult mvcResult = mockMvc
                .perform(
                        post("/thirdparty")
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("American Express"));
        //assertTrue(courseRepository.existsById("CS104"));
    }

    @Test
    void charge() {
    }

    @Test
    void refund() {
    }

    @Test
    void deleteThirdParty() {
    }
}