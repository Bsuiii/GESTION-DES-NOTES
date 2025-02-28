package com.example.school_managment_system.exceptions.usersExceptions;

public class UserException extends Exception{

    public UserException(UserErrMessage userErrMessage){
        super(userErrMessage.getMessage());
    }
}
