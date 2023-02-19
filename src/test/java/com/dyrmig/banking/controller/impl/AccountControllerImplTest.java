package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.*;
import com.dyrmig.banking.model.*;
import com.dyrmig.banking.repository.*;
import com.dyrmig.banking.service.interfaces.ThirdPartyService;
import com.dyrmig.banking.service.interfaces.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
class AccountControllerImplTest {
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
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private ThirdParty thirdParty1;
    private ThirdParty thirdParty2;
    private AccountHolder accountHolder1;
    private Checking checking1, checking2;
    private Admin admin1;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;

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
                "password1234",
                LocalDate.of(1986, 10, 26),
                new Address("Calle 13", "13","Barcelona", "Barcelona", "91234", "Spain")
        );
        accountHolder1.setPassword(passwordEncoder.encode(accountHolder1.getPassword()));
        accountHolderRepository.save(accountHolder1);

        checking1 = new Checking(new Money(new BigDecimal("6000")), "chcktsd82h8e", accountHolder1);
        checkingRepository.save(checking1);
        checking2 = new Checking(new Money(new BigDecimal("1000")), "secret82h8e", accountHolder1);
        checkingRepository.save(checking2);

        accountHolder1.setOwnedAccountList(List.of(checking1));
        accountHolderRepository.save(accountHolder1);
    }

    @AfterEach
    void tearDown() {
        accountHolder1.setOwnedAccountList(null);
        checkingRepository.deleteAll();
        savingsRepository.deleteAll();
        creditCardRepository.deleteAll();
        accountHolderRepository.deleteAll();
        thirdPartyRepository.deleteAll();
        adminRepository.deleteAll();
    }
    @Test
    void saveChecking() throws Exception {
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

        Checking newChecking = new Checking(new Money(new BigDecimal("1000")), "secret1234", null);

        objectMapper.registerModule(new JavaTimeModule());
        String body = objectMapper.writeValueAsString(newChecking);

        mvcResult = mockMvc
                .perform(
                        post("/accountholders/"+accountHolder1.getId()+"/checking")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.accountHolderWithAccounts(accountHolder1.getId());
        assertEquals(3, optionalAccountHolder.get().getOwnedAccountList().size());
    }

    @Test
    void saveSavings() throws Exception {
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

        Savings newSavings = new Savings(new Money(new BigDecimal("1000")), "secret123456", null, null, null);

        objectMapper.registerModule(new JavaTimeModule());
        String body = objectMapper.writeValueAsString(newSavings);

        mvcResult = mockMvc
                .perform(
                        post("/accountholders/"+accountHolder1.getId()+"/savings")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.accountHolderWithAccounts(accountHolder1.getId());
        assertEquals(3, optionalAccountHolder.get().getOwnedAccountList().size());
    }

    @Test
    void saveCreditCard() throws Exception {
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

        CreditCard newCreditCard = new CreditCard(new Money(new BigDecimal("1000")), "secret123456", accountHolder1, null, null);

        objectMapper.registerModule(new JavaTimeModule());
        String body = objectMapper.writeValueAsString(newCreditCard);

        mvcResult = mockMvc
                .perform(
                        post("/accountholders/"+accountHolder1.getId()+"/creditcard")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.accountHolderWithAccounts(accountHolder1.getId());
        assertEquals(3, optionalAccountHolder.get().getOwnedAccountList().size());
    }

    @Test
    void transfer() throws Exception {
        // Login
        MvcResult mvcResult = mockMvc.perform(
                        get("/login")
                                .param("username", "loloperez")
                                .param("password", "password1234")
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

        TransferForm transferForm = new TransferForm(new Money(new BigDecimal("1000.00")), checking2.getId(), "Lolo Perez");
        String body = objectMapper.writeValueAsString(transferForm);

        mvcResult = mockMvc
                .perform(
                        post("/accountholders/"+accountHolder1.getId()+"/accounts/"+checking1.getId()+"/transfer")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Account> optionalAccount1 = accountRepository.findById(checking1.getId());
        Optional<Account> optionalAccount2 = accountRepository.findById(checking2.getId());
        assertEquals(new BigDecimal("5000.00"), optionalAccount1.get().getBalance().getAmount());
        assertEquals(new BigDecimal("2000.00"), optionalAccount2.get().getBalance().getAmount());
    }

    @Test
    void subtractBalance() throws Exception {
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

        AmountOfOperationDTO amountOfOperationDTO = new AmountOfOperationDTO(new BigDecimal("1000.00"));
        String body = objectMapper.writeValueAsString(amountOfOperationDTO);

        mvcResult = mockMvc
                .perform(
                        patch("/accounts/"+ checking1.getId()+"/subtract")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Account> optionalAccount = accountRepository.findById(checking1.getId());
        assertEquals(new BigDecimal("5000.00"), optionalAccount.get().getBalance().getAmount());
    }

    @Test
    void addBalance() throws Exception {
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

        AmountOfOperationDTO amountOfOperationDTO = new AmountOfOperationDTO(new BigDecimal("1000.00"));
        String body = objectMapper.writeValueAsString(amountOfOperationDTO);

        mvcResult = mockMvc
                .perform(
                        patch("/accounts/"+ checking1.getId()+"/add")
                                .headers(httpHeaders)
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        Optional<Account> optionalAccount = accountRepository.findById(checking1.getId());
        assertEquals(new BigDecimal("7000.00"), optionalAccount.get().getBalance().getAmount());
    }

    @Test
    void getAccount() throws Exception {
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
                .perform(get("/accounts/"+checking1.getId()).headers(httpHeaders))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().contains("chcktsd82h8e"));
    }
}