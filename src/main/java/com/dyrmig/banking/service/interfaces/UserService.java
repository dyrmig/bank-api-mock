package com.dyrmig.banking.service.interfaces;

import com.dyrmig.banking.model.Account;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Admin;
import com.dyrmig.banking.model.User;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    User saveAdmin(Admin admin);
    AccountHolder saveAccountHolder(AccountHolder accountHolder);
    AccountHolder getAccountHolder(Long accountHolderId, Authentication authentication);
    List<Account> getAccountHolderAccounts(Long accountHolderId, Authentication authentication);
    List<User> getUsers();

}