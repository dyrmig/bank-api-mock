package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.Address;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.classes.ThirdPartyOperationDTO;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Admin;
import com.dyrmig.banking.model.Checking;
import com.dyrmig.banking.model.ThirdParty;
import com.dyrmig.banking.repository.AccountHolderRepository;
import com.dyrmig.banking.repository.AdminRepository;
import com.dyrmig.banking.repository.CheckingRepository;
import com.dyrmig.banking.repository.ThirdPartyRepository;
import com.dyrmig.banking.service.interfaces.ThirdPartyService;
import com.dyrmig.banking.service.interfaces.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
    private AdminRepository adminRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    //@Autowired
    //private WebApplicationContext webApplicationContext; //simula un server
    @Autowired
    private MockMvc mockMvc; //simula peticiones HTTP
    private final ObjectMapper objectMapper = new ObjectMapper(); //construir objetos json de las clases de java

    private ThirdParty thirdParty1;
    private ThirdParty thirdParty2;
    private AccountHolder accountHolder1;
    private Checking checking1;
    private Admin admin1;

    @BeforeEach
    void setUp() {
        //mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        admin1 = new Admin("Admin Admin", "admin", "admin1234");
        admin1.setPassword(passwordEncoder.encode(admin1.getPassword()));
        adminRepository.save(admin1);

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

        checking1 = new Checking(new Money(new BigDecimal("6000")), "chcktsd82h8e", accountHolder1);
        checkingRepository.save(checking1);

        accountHolder1.setOwnedAccountList(List.of(checking1));
        accountHolderRepository.save(accountHolder1);
    }

    @AfterEach
    void tearDown() {
        accountHolder1.setOwnedAccountList(null);
        checkingRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }
    @Test
    void findAll() throws Exception {
        // Login
        MvcResult mvcResult = mockMvc.perform(
                        get("/login")
                                .param("username", "admin")
                                .param("password", "admin1234")
                )
                .andExpect(status().isOk())
                .andReturn();
        // Parse response to JSON
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        // Get access token
        String token = jsonObject.getString("access_token");
        //add token to the http header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

         mvcResult = mockMvc
                .perform(get("/thirdparty").headers(httpHeaders)) //hicimos un static import de MockMvcRequestBuilders
                .andExpect(status().isOk()) //importamos: import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) //importamos: import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("MasterCard"));
        assertTrue(mvcResult.getResponse().getContentAsString().contains("Visa"));
    }
    @Test
    void saveThirdParty() throws Exception {
        // Login
        MvcResult mvcResult = mockMvc.perform(
                        get("/login")
                                .param("username", "admin")
                                .param("password", "admin1234")
                )
                .andExpect(status().isOk())
                .andReturn();
        // Parse response to JSON
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        // Get access token
        String token = jsonObject.getString("access_token");
        //add token to the http header
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        ThirdParty newThirdParty = new ThirdParty("American Express");
        String body = objectMapper.writeValueAsString(newThirdParty); //convertimos el objeto java a un string json

        mvcResult = mockMvc
                .perform(
                        post("/thirdparty")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        assertTrue(mvcResult.getResponse().getContentAsString().contains("American Express"));
    }

    @Test
    void charge() throws Exception {
        ThirdPartyOperationDTO thirdPartyOperationDTO = new ThirdPartyOperationDTO(new BigDecimal("1000.00"), "chcktsd82h8e");
        String body = objectMapper.writeValueAsString(thirdPartyOperationDTO); //convertimos el objeto java a un string json

        MvcResult mvcResult = mockMvc
                .perform(
                        post("/thirdparty/"+checking1.getId()+"/charge")
                                .header("hashed-key", thirdParty1.getHashedKey())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        Optional<Checking> checkingOptional = checkingRepository.findById(checking1.getId());
        assertTrue(checkingOptional.isPresent());
        assertEquals(new BigDecimal("5000.00"), checkingOptional.get().getBalance().getAmount());
    }

    @Test
    void refund() throws Exception {
        ThirdPartyOperationDTO thirdPartyOperationDTO = new ThirdPartyOperationDTO(new BigDecimal("1000.00"), "chcktsd82h8e");
        String body = objectMapper.writeValueAsString(thirdPartyOperationDTO); //convertimos el objeto java a un string json

        MvcResult mvcResult = mockMvc
                .perform(
                        post("/thirdparty/"+checking1.getId()+"/refund")
                                .header("hashed-key", thirdParty1.getHashedKey())
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        Optional<Checking> checkingOptional = checkingRepository.findById(checking1.getId());
        assertTrue(checkingOptional.isPresent());
        assertEquals(new BigDecimal("7000.00"), checkingOptional.get().getBalance().getAmount());
    }

    @Test
    void deleteThirdParty() throws Exception {
        // Login
        MvcResult mvcResult = mockMvc.perform(
                        get("/login")
                                .param("username", "admin")
                                .param("password", "admin1234")
                )
                .andExpect(status().isOk())
                .andReturn();
        // Parse response to JSON
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());
        // Get access token
        String token = jsonObject.getString("access_token");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        mvcResult = mockMvc.perform(delete("/thirdparty/"+thirdParty1.getId()).headers(httpHeaders))
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<ThirdParty> thirdPartyOptional = thirdPartyRepository.findById(thirdParty1.getId());
        assertFalse(thirdPartyOptional.isPresent());
    }
}