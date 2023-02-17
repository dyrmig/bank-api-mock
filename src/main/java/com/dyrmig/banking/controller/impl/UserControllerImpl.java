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
    @ResponseStatus(HttpStatus.OK)
    public List<User> getUsers() {
        return userService.getUsers();
    }
    @GetMapping("/accountholders/{accountHolderId}/accounts") //get all accounts of one accountHolder
    @ResponseStatus(HttpStatus.OK)
    public List<Account> getAccountHolderWithAccounts(@PathVariable(name = "accountHolderId") Long accountHolderId, Authentication authentication){
        return userService.getAccountHolderAccounts(accountHolderId, authentication);
    }
    @GetMapping("/accountholders/{accountHolderId}")
    @ResponseStatus(HttpStatus.OK)
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
    public Admin saveAdmin(@RequestBody Admin admin) {
        return userService.saveAdmin(admin);
    }
    @PostMapping("/accountholders")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountHolder saveAccountHolder(@RequestBody AccountHolder accountHolder) {
        return userService.saveAccountHolder(accountHolder);
    }
}
