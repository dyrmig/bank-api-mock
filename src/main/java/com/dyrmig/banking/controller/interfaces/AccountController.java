package com.dyrmig.banking.controller.interfaces;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.classes.TransferForm;
import com.dyrmig.banking.model.Account;
import com.dyrmig.banking.model.Checking;
import com.dyrmig.banking.model.CreditCard;
import com.dyrmig.banking.model.Savings;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;

public interface AccountController {
    Account saveChecking(Checking checking, Long accountHolderId);
    Account saveSavings(Savings savings, Long accountHolderId);
    Account saveCreditCard(CreditCard creditCard, Long accountHolderId);
    void transfer(Long accountHolderId, Long accountId, TransferForm transferForm, Authentication authentication);
    void subtractBalance(Long accountId, AmountOfOperationDTO amountOfOperationDTO);
    void addBalance(Long accountId, AmountOfOperationDTO amountOfOperationDTO);
    Account getAccount(Long accountId, Authentication authentication);
}
