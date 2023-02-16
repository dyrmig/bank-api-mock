package com.dyrmig.banking.controller.impl;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.classes.TransferForm;
import com.dyrmig.banking.controller.interfaces.AccountController;
import com.dyrmig.banking.model.*;
import com.dyrmig.banking.repository.AccountRepository;
import com.dyrmig.banking.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class AccountControllerImpl implements AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/accountholders/{accountHolderId}/checking")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveChecking(@RequestBody Checking checking, @PathVariable(name = "accountHolderId") Long accountHolderId) {
        accountService.saveChecking(checking, accountHolderId);
    }

    @PostMapping("/accountholders/{accountHolderId}/savings")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveSavings(@RequestBody Savings savings, @PathVariable(name = "accountHolderId") Long accountHolderId) {
        accountService.saveSavings(savings, accountHolderId);
    }

    @PostMapping("/accountholders/{accountHolderId}/creditcard")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveCreditCard(@RequestBody CreditCard creditCard, @PathVariable(name = "accountHolderId") Long accountHolderId) {
        accountService.saveCreditCard(creditCard, accountHolderId);
    }

    @PostMapping("/accountholders/{accountHolderId}/accounts/{accountId}/transfer")
    public void transfer(@PathVariable(name = "accountHolderId") Long accountHolderId, @PathVariable(name = "accountId") Long accountId, @RequestBody TransferForm transferForm, Authentication authentication) {
        accountService.transfer(accountHolderId, accountId, transferForm, authentication);
    }

    @PatchMapping("/accounts/{accountId}/subtract")
    public void subtractBalance(@PathVariable(name = "accountId") Long accountId, @RequestBody AmountOfOperationDTO amountOfOperationDTO){
        accountService.subtractBalance(accountId, amountOfOperationDTO);
    }
    @PatchMapping("/accounts/{accountId}/add")
    public void addBalance(@PathVariable(name = "accountId") Long accountId, @RequestBody AmountOfOperationDTO amountOfOperationDTO){
        accountService.addBalance(accountId, amountOfOperationDTO);
    }
    @GetMapping("/accounts/{accountId}")
    public Account getAccount(@PathVariable(name = "accountId") Long accountId, Authentication authentication){
        return accountService.getAccount(accountId, authentication);
    }
}
