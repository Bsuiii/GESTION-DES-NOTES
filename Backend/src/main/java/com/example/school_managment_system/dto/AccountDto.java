package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Account;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class AccountDto {
    private int id;
    private String login;
    private String password;
    private boolean is_active;
    private boolean is_locked;
    private int userId;
    private int roleId;

    // Convert Account entity to AccountDto
    public AccountDto accountToDto(Account account) {
        AccountDto accountDto = new AccountDto();
        accountDto.setId(account.getId());
        accountDto.setLogin(account.getLogin());
        accountDto.setPassword(account.getPassword());
        accountDto.set_active(account.is_active());
        accountDto.set_locked(account.is_locked());
        accountDto.setUserId(account.getUser().getId());
        accountDto.setRoleId(account.getRole().getId());
        return accountDto;
    }

    // Convert List of Account entities to List of AccountDto
    public List<AccountDto> accountsToDto(List<Account> accounts) {
        return accounts.stream()
                .map(this::accountToDto)
                .collect(Collectors.toList());
    }
}