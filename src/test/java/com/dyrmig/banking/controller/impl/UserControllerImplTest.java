package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.model.Admin;
import com.dyrmig.banking.repository.AdminRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerImplTest {

    //@Autowired
  //  private WebApplicationContext webApplicationContext; //simula un server
    @Autowired
    private MockMvc mockMvc; //simula peticiones HTTP
    private final ObjectMapper objectMapper = new ObjectMapper(); //construir objetos json de las clases de java
    @Autowired
    AdminRepository adminRepository;

    @BeforeEach
    void setUp() {
       // mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void saveAdmin() throws Exception {
        //Admin creation
        Admin newAdmin = new Admin("Admin Admin", "admin2", "admin1234");
        String body = objectMapper.writeValueAsString(newAdmin); //convertimos el objeto java a un string json

        MvcResult mvcResult2 = mockMvc
                .perform(
                        post("/admins")
                                .content(body)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        List<Admin> adminList = adminRepository.findAll();
        assertEquals(1, adminList.size());







        // Login
        MvcResult mvcResult = mockMvc.perform(
                        get("/login")
                                .param("username", "admin2")
                                .param("password", "admin1234")
                )
                .andExpect(status().isOk())
                .andReturn();
        // Parse response to JSON
        JSONObject jsonObject = new JSONObject(mvcResult.getResponse().getContentAsString());

        // Get access token
        String token = jsonObject.getString("access_token");
    }
}