package com.example.school_managment_system.exceptions.accountsException;

public class AccountException extends Exception{

    public AccountException(AccountErrMsg accountErrMsg){
        super(accountErrMsg.getMessage());
    }
}
