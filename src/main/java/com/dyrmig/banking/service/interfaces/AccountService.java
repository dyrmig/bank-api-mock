package com.dyrmig.banking.service.interfaces;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.TransferForm;
import com.dyrmig.banking.model.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

public interface AccountService {
    Account saveChecking(Checking checking, Long accountHolderId);
    Account saveSavings(Savings savings, Long accountHolderId);
    Account saveCreditCard(CreditCard creditCard, Long accountHolderId);
    void transfer(Long accountHolderId, Long accountId, TransferForm transferForm, Authentication authentication);
    void subtractBalance(Long accountId, AmountOfOperationDTO amountOfOperationDTO);
    void addBalance(Long accountId, AmountOfOperationDTO amountOfOperationDTO);
    Account getAccount(Long accountId, Authentication authentication);
}
