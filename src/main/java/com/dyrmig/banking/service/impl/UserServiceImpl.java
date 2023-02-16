package com.dyrmig.banking.service.impl;

import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.model.Account;
import com.dyrmig.banking.model.AccountHolder;
import com.dyrmig.banking.model.Admin;
import com.dyrmig.banking.model.User;
import com.dyrmig.banking.repository.AccountHolderRepository;
import com.dyrmig.banking.repository.AdminRepository;
import com.dyrmig.banking.repository.RoleRepository;
import com.dyrmig.banking.repository.UserRepository;
import com.dyrmig.banking.service.interfaces.UserService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        // Encode the user's password for security before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    @Override
    public User saveAdmin(Admin admin) {
        Optional<User> userOptional = userRepository.findByUsername(admin.getUsername());
        if(userOptional.isPresent()){
            throw new MyCustomException("Username already exists");
        }
        // Encode the user's password for security before saving
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }
    @Override
    public AccountHolder saveAccountHolder(AccountHolder accountHolder) {
        Optional<User> userOptional = userRepository.findByUsername(accountHolder.getUsername());
        if(userOptional.isPresent()){
            throw new MyCustomException("Username already exists");
        }
        // Encode the user's password for security before saving
        accountHolder.setPassword(passwordEncoder.encode(accountHolder.getPassword()));
        return accountHolderRepository.save(accountHolder);
    }
    @Override
    public AccountHolder getAccountHolder(Long accountHolderId, Authentication authentication){
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        Optional<User> optionalUser = userRepository.findByUsername(authentication.getName());
        if(!optionalUser.isPresent()){
            throw new MyCustomException("User not found");
        }
        if(isUser && !isAdmin){
            if(accountHolderId != optionalUser.get().getId()){
                throw new MyCustomException("User don't have access");
            }
        }

        Optional<AccountHolder> accountHolderOptional = accountHolderRepository.findById(accountHolderId);
        if(!accountHolderOptional.isPresent()){
            throw new MyCustomException("Account holder not found");
        }
        return accountHolderOptional.get();
    }
    @Override
    public List<Account> getAccountHolderAccounts(Long accountHolderId, Authentication authentication){
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        Optional<User> optionalUser = userRepository.findByUsername(authentication.getName());
        if(!optionalUser.isPresent()){
            throw new MyCustomException("User not found");
        }
        if(isUser && !isAdmin){
            if(accountHolderId != optionalUser.get().getId()){
                throw new MyCustomException("User don't have access");
            }
        }

        Optional<AccountHolder> accountHolderOptionalPrincipalOwned = accountHolderRepository.accountHolderWithAccounts(accountHolderId);
        Optional<AccountHolder> accountHolderOptionalSecondaryOwned = accountHolderRepository.accountHolderWithSecondaryAccounts(accountHolderId);
        if(!accountHolderOptionalPrincipalOwned.isPresent() &&
                !accountHolderOptionalSecondaryOwned.isPresent()){
            throw new MyCustomException("Account holder have no associated accounts");
        }
        if(accountHolderOptionalPrincipalOwned.isPresent() &&
                accountHolderOptionalSecondaryOwned.isPresent()){
            List<Account> principalOwner = accountHolderOptionalPrincipalOwned.get().getOwnedAccountList();
            List<Account> secondaryOwner = accountHolderOptionalSecondaryOwned.get().getSecondaryOwnedAccountList();
            List<Account> combinedAccounts = new ArrayList<>(principalOwner);
            combinedAccounts.addAll(secondaryOwner);
            return  combinedAccounts;
        }
        if(accountHolderOptionalPrincipalOwned.isPresent()){
            return accountHolderOptionalPrincipalOwned.get().getOwnedAccountList();
        } else {
            return accountHolderOptionalSecondaryOwned.get().getSecondaryOwnedAccountList();
        }
    }
    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
