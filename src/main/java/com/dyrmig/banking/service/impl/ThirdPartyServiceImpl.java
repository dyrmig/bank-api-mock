package com.dyrmig.banking.service.impl;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.model.Account;
import com.dyrmig.banking.model.ThirdParty;
import com.dyrmig.banking.repository.AccountRepository;
import com.dyrmig.banking.repository.ThirdPartyRepository;
import com.dyrmig.banking.service.interfaces.ThirdPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ThirdPartyServiceImpl implements ThirdPartyService {
    @Autowired
    ThirdPartyRepository thirdPartyRepository;
    @Autowired
    AccountRepository accountRepository;
    @Override
    public void saveThirdParty(ThirdParty thirdParty) {
        if (thirdParty.getName() == null || thirdParty.getName().isEmpty() || thirdParty.getName().isBlank()){
            throw new MyCustomException("Name cannot be empty");
        }
        ThirdParty newThirdParty = new ThirdParty(thirdParty.getName());
        thirdPartyRepository.save(newThirdParty);
    }
    @Override
    public void charge(Long accountId, AmountOfOperationDTO amountOfOperationDTO, String hashedKey){
        Optional<ThirdParty> thirdPartyOptional = thirdPartyRepository.findByHashedKey(hashedKey);
        if(!thirdPartyOptional.isPresent()){
            throw new MyCustomException("ThirdParty not found");
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw new MyCustomException("Target account not found");
        }
        if(accountOptional.get().getBalance().getAmount().compareTo(amountOfOperationDTO.amountOfOperation) < 0){
            throw new MyCustomException("Account don't have enough funds for this operation");
        }

        accountOptional.get().getBalance().decreaseAmount(amountOfOperationDTO.amountOfOperation);
        accountRepository.save(accountOptional.get());
    }
    @Override
    public void refund(Long accountId, AmountOfOperationDTO amountOfOperationDTO, String hashedKey){
        Optional<ThirdParty> thirdPartyOptional = thirdPartyRepository.findByHashedKey(hashedKey);
        if(!thirdPartyOptional.isPresent()){
            throw new MyCustomException("ThirdParty not found");
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw new MyCustomException("Target account not found");
        }

        accountOptional.get().getBalance().increaseAmount(amountOfOperationDTO.amountOfOperation);
        accountRepository.save(accountOptional.get());
    }
}
