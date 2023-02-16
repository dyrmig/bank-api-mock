package com.dyrmig.banking.controller.interfaces;

import com.dyrmig.banking.model.Account;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Admin;
import com.dyrmig.banking.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface UserController {
    List<User> getUsers();
    String test();
    AccountHolder getAccountHolder(Long accountHolderId, Authentication authentication);
    List<Account> getAccountHolderWithAccounts(Long accountHolderId, Authentication authentication);
    void saveUser(User user);
    void saveAdmin(Admin admin);
    void saveAccountHolder(AccountHolder accountHolder);
}
