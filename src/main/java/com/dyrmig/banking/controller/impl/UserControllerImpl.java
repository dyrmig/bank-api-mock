package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.controller.interfaces.UserController;
import com.dyrmig.banking.model.Account;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Admin;
import com.dyrmig.banking.model.User;
import com.dyrmig.banking.repository.AccountHolderRepository;
import com.dyrmig.banking.service.interfaces.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class UserControllerImpl implements UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/test")
    public String test() {
        return "hello this is a test";
    }

    @GetMapping("/test2")
    public String test2(Authentication authentication) {

        return "Hello, " + authentication.getName() + " " + authentication.getAuthorities();
        //return "hello this is a test 2";
    }

    @GetMapping("/accountholders/{accountHolderId}/accounts") //get all accounts of one accountHolder
    public List<Account> getAccountHolderWithAccounts(@PathVariable(name = "accountHolderId") Long accountHolderId, Authentication authentication){
        return userService.getAccountHolderAccounts(accountHolderId, authentication);
    }
    @GetMapping("/accountholders/{accountHolderId}")
    public AccountHolder getAccountHolder(@PathVariable(name = "accountHolderId") Long accountHolderId, Authentication authentication){
        return userService.getAccountHolder(accountHolderId, authentication);
    }
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@RequestBody User user) {
        userService.saveUser(user);
    }
    @PostMapping("/admins")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAdmin(@RequestBody Admin admin) {
        userService.saveAdmin(admin);
    }
    @PostMapping("/accountholders")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveAccountHolder(@RequestBody AccountHolder accountHolder) {
        userService.saveAccountHolder(accountHolder);
    }
}
