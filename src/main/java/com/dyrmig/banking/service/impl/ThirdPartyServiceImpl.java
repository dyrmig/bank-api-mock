package com.dyrmig.banking.service.impl;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.classes.ThirdPartyOperationDTO;
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
    public ThirdParty saveThirdParty(ThirdParty thirdParty) {
        if (thirdParty.getName() == null || thirdParty.getName().isEmpty() || thirdParty.getName().isBlank()){
            throw new MyCustomException("Name cannot be empty");
        }
        ThirdParty newThirdParty = new ThirdParty(thirdParty.getName());
        return thirdPartyRepository.save(newThirdParty);
    }
    @Override
    public void charge(Long accountId, ThirdPartyOperationDTO thirdPartyOperationDTO, String hashedKey){
        Optional<ThirdParty> thirdPartyOptional = thirdPartyRepository.findByHashedKey(hashedKey);
        if(!thirdPartyOptional.isPresent()){
            throw new MyCustomException("ThirdParty not found");
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw new MyCustomException("Target account not found");
        }
        if(!thirdPartyOperationDTO.getTargetAccountSecretKey().equals(accountOptional.get().getSecretKey())){
            throw new MyCustomException("Target account SecretKey is not valid");
        }
        if(accountOptional.get().getBalance().getAmount().compareTo(thirdPartyOperationDTO.getAmountOfOperation()) < 0){
            throw new MyCustomException("Account don't have enough funds for this operation");
        }

        accountOptional.get().getBalance().decreaseAmount(thirdPartyOperationDTO.getAmountOfOperation());
        accountRepository.save(accountOptional.get());
    }
    @Override
    public void refund(Long accountId, ThirdPartyOperationDTO thirdPartyOperationDTO, String hashedKey){
        Optional<ThirdParty> thirdPartyOptional = thirdPartyRepository.findByHashedKey(hashedKey);
        if(!thirdPartyOptional.isPresent()){
            throw new MyCustomException("ThirdParty not found");
        }
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw new MyCustomException("Target account not found");
        }
        if(!thirdPartyOperationDTO.getTargetAccountSecretKey().equals(accountOptional.get().getSecretKey())){
            throw new MyCustomException("Target account SecretKey is not valid");
        }

        accountOptional.get().getBalance().increaseAmount(thirdPartyOperationDTO.getAmountOfOperation());
        accountRepository.save(accountOptional.get());
    }
    @Override
    public void deleteThirdParty(Long thirdPartyId){
        Optional<ThirdParty> thirdPartyOptional = thirdPartyRepository.findById(thirdPartyId);
        if(!thirdPartyOptional.isPresent()){
            throw new MyCustomException("ThirdParty not found");
        }
        thirdPartyRepository.delete(thirdPartyOptional.get());
    }
}
