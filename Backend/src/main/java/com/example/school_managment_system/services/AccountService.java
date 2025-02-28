package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.AccountDto;
import com.example.school_managment_system.exceptions.accountsException.AccountErrMsg;
import com.example.school_managment_system.exceptions.accountsException.AccountException;
import com.example.school_managment_system.models.Account;
import com.example.school_managment_system.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    // Create Account
    public Account createAccount(Account account) throws AccountException {
        if (accountRepository.existsByLogin(account.getLogin())) {
            throw new AccountException(AccountErrMsg.LOGIN_ALREADY_EXISTS);
        }
        return accountRepository.save(account);
    }

    // Get All Accounts
    public List<AccountDto> listAccounts() {
        List<Account> accounts = accountRepository.findAll();
        AccountDto accountDto = new AccountDto();
        try {
            return accountDto.accountsToDto(accounts);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    // Get Account by ID
    public AccountDto getAccountById(int id) throws AccountException {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(AccountErrMsg.ACCOUNT_NOT_FOUND));
        return new AccountDto().accountToDto(account);
    }

    // Update Account
    public Account updateAccount(int id, Account accountDetails) throws AccountException {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(AccountErrMsg.ACCOUNT_NOT_FOUND));

        account.setLogin(accountDetails.getLogin());
        account.setPassword(accountDetails.getPassword());
        account.set_active(accountDetails.is_active());
        account.set_locked(accountDetails.is_locked());
        account.setUser(accountDetails.getUser());
        account.setRole(accountDetails.getRole());

        return accountRepository.save(account);
    }

    // Delete Account
    public void deleteAccount(int id) throws AccountException {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountException(AccountErrMsg.ACCOUNT_NOT_FOUND));
        accountRepository.delete(account);
    }
}