package com.example.school_managment_system.exceptions.accountsException;

import lombok.Getter;

@Getter
public enum AccountErrMsg {

    INVALID_CREDENTIALS("Invalid Email Or Password"),
    ACCOUNT_NOT_FOUND("Account not found"),
    LOGIN_ALREADY_EXISTS("Login already exists");

    private final String message;

    AccountErrMsg(String message){
        this.message=message;
    }

}
