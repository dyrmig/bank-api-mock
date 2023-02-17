package com.dyrmig.banking.service.impl;

import com.dyrmig.banking.classes.AmountOfOperationDTO;
import com.dyrmig.banking.classes.Money;
import com.dyrmig.banking.classes.MyCustomException;
import com.dyrmig.banking.classes.TransferForm;
import com.dyrmig.banking.model.*;
import com.dyrmig.banking.repository.*;
import com.dyrmig.banking.service.interfaces.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private CheckingRepository checkingRepository;
    @Autowired
    private StudentCheckingRepository studentCheckingRepository;
    @Autowired
    private SavingsRepository savingsRepository;
    @Autowired
    private AccountHolderRepository accountHolderRepository;
    @Autowired
    private CreditCardRepository creditCardRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Account saveChecking(Checking checking, Long accountHolderId) {
        //get the target account holder
        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.findById(accountHolderId);
        //check if the account holder exists
        if(!optionalAccountHolder.isPresent()){
            throw new MyCustomException("Account holder not found");
        }

        //verifying if the accountHolder is younger than 24 to determine if StudentChecking is created instead of Checking
        long years = ChronoUnit.YEARS.between(optionalAccountHolder.get().getDateOfBirth(), LocalDate.now());
        if (years >= 24) {
            Checking newChecking = new Checking(checking.getBalance(),checking.getSecretKey(),optionalAccountHolder.get());
            checkingRepository.save(newChecking);
            List<Account> accountList = new ArrayList<>();
            accountList.add(newChecking);
            optionalAccountHolder.get().setOwnedAccountList(accountList);
            accountHolderRepository.save(optionalAccountHolder.get());
            return checkingRepository.save(newChecking);
        } else {
            StudentChecking newChecking = new StudentChecking(checking.getBalance(),checking.getSecretKey(),optionalAccountHolder.get());
            studentCheckingRepository.save(newChecking);
            List<Account> accountList = new ArrayList<>();
            accountList.add(newChecking);
            optionalAccountHolder.get().setOwnedAccountList(accountList);
            accountHolderRepository.save(optionalAccountHolder.get());
            return studentCheckingRepository.save(newChecking);
        }
    }

    @Override
    public Account saveSavings(Savings savings, Long accountHolderId) {
        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.findById(accountHolderId);
        if(!optionalAccountHolder.isPresent()){
            throw new MyCustomException("Account holder not found");
        }
        Savings newSavings = new Savings(savings.getBalance(), savings.getSecretKey(), optionalAccountHolder.get(), savings.getInterestRate(), savings.getMinimumBalance());
        savingsRepository.save(newSavings);
        List<Account> accountList = new ArrayList<>();
        accountList.add(newSavings);
        optionalAccountHolder.get().setOwnedAccountList(accountList);
        accountHolderRepository.save(optionalAccountHolder.get());
        return savingsRepository.save(newSavings);
    }

    @Override
    public Account saveCreditCard(CreditCard creditCard, Long accountHolderId) {
        Optional<AccountHolder> optionalAccountHolder = accountHolderRepository.findById(accountHolderId);
        if(!optionalAccountHolder.isPresent()){
            throw new MyCustomException("Account holder not found");
        }
        CreditCard newCreditCard = new CreditCard(creditCard.getBalance(), creditCard.getSecretKey(), optionalAccountHolder.get(), creditCard.getCreditLimit(), creditCard.getInterestRate());
        creditCardRepository.save(newCreditCard);
        List<Account> accountList = new ArrayList<>();
        accountList.add(newCreditCard);
        optionalAccountHolder.get().setOwnedAccountList(accountList);
        accountHolderRepository.save(optionalAccountHolder.get());
        return creditCardRepository.save(newCreditCard);
    }

    @Override
    public void transfer(Long accountHolderId, Long accountId, TransferForm transferForm, Authentication authentication){
        Optional<AccountHolder> authAccountHolderOptional = accountHolderRepository.findByUsername(authentication.getName());
        Optional<Account> fromAccountOptional = accountRepository.findById(accountId);
        Optional<Account> toAccountOptional = accountRepository.findById(transferForm.getRecipientAccountId());

        if(authAccountHolderOptional.get().getId() != fromAccountOptional.get().getPrimaryOwner().getId() &&
                fromAccountOptional.get().getSecondaryOwner() == null){
            throw new MyCustomException("User don't have rights to the source account");
        }
        if(fromAccountOptional.get().getSecondaryOwner() != null &&
                authAccountHolderOptional.get().getId() != fromAccountOptional.get().getSecondaryOwner().getId()){
            throw new MyCustomException("User don't have rights to the source account");
        }


        if(!fromAccountOptional.isPresent()){
            throw new MyCustomException("Sender account not found");
        } else if(!toAccountOptional.isPresent()){
            throw new MyCustomException("Recipient account not found");
        }

        if(fromAccountOptional.get().getBalance().getAmount().compareTo(transferForm.getAmount().getAmount()) < 0 ){
            throw new MyCustomException("Sender account don't have enough balance");
        }


        fromAccountOptional.get().getBalance().decreaseAmount(transferForm.getAmount().getAmount());
        toAccountOptional.get().getBalance().increaseAmount(transferForm.getAmount().getAmount());

        accountRepository.save(fromAccountOptional.get());
        accountRepository.save(toAccountOptional.get());

        //checking if the sending account is subjected to a minimum balance penalty
        Account account = fromAccountOptional.get();
        if(account instanceof Checking){
            Checking checking = (Checking) account;
            checking.checkBalanceReachedMinimum();
            checkingRepository.save(checking);
        } else if (account instanceof Savings) {
            Savings savings = (Savings) account;
            savings.checkBalanceReachedMinimum();
            savingsRepository.save(savings);
        }
    }
    @Override
    public void subtractBalance(Long accountId, AmountOfOperationDTO amountOfOperationDTO){
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw new MyCustomException("Account not found");
        }
        if(accountOptional.get().getBalance().getAmount().compareTo(amountOfOperationDTO.getAmountOfOperation()) < 0){
            throw new MyCustomException("Account don't have enough funds for this operation");
        }

        accountOptional.get().getBalance().decreaseAmount(amountOfOperationDTO.getAmountOfOperation());
        accountRepository.save(accountOptional.get());

        //checking if the account is subjected to a minimum balance penalty
        Account account = accountOptional.get();
        if(account instanceof Checking){
            Checking checking = (Checking) account;
            checking.checkBalanceReachedMinimum();
            checkingRepository.save(checking);
        } else if (account instanceof Savings) {
            Savings savings = (Savings) account;
            savings.checkBalanceReachedMinimum();
            savingsRepository.save(savings);
        }
    }
    @Override
    public void addBalance(Long accountId, AmountOfOperationDTO amountOfOperationDTO){
        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw new MyCustomException("Account not found");
        }

        accountOptional.get().getBalance().increaseAmount(amountOfOperationDTO.getAmountOfOperation());
        accountRepository.save(accountOptional.get());
    }
    @Override
    public Account getAccount(Long accountId, Authentication authentication){
        //checking the roles of the authenticated user
        boolean isUser = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        Optional<User> optionalUser = userRepository.findByUsername(authentication.getName());
        if(!optionalUser.isPresent()){
            throw new MyCustomException("User not found");
        }

        Optional<Account> accountOptional = accountRepository.findById(accountId);
        if(!accountOptional.isPresent()){
            throw  new MyCustomException("Account not found");
        }
        //checking if the account belongs to the authenticated user if the user don't have ADMIN in his roles
        if(isUser && !isAdmin){
            if(accountOptional.get().getPrimaryOwner().getId() != optionalUser.get().getId() &&
                    accountOptional.get().getSecondaryOwner() == null){
                throw new MyCustomException("User don't have access");
            }
            if(accountOptional.get().getSecondaryOwner() != null &&
                    accountOptional.get().getSecondaryOwner().getId() != optionalUser.get().getId()){
                throw new MyCustomException("User don't have access");
            }
        }

        //checking if the account is subjected to the application of the interest
        Account account = accountOptional.get();
        if(account instanceof CreditCard){
            CreditCard creditCard = (CreditCard) account;
            creditCard.getBalance(); //inside getBalance is the logic of interest application
            creditCardRepository.save(creditCard);
        } else if (account instanceof Savings) {
            Savings savings = (Savings) account;
            savings.getBalance(); //inside getBalance is the logic of interest application
            savingsRepository.save(savings);
        }
        //query for the account again in case some interest has been applied
        accountOptional = accountRepository.findById(accountId);
        return accountOptional.get();
    }
}
