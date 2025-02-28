package com.example.school_managment_system.exceptions.usersExceptions;

import lombok.Getter;

@Getter
public enum UserErrMessage {
    User_NOT_FOUND("User not found"),
    User_ALREADY_EXISTS("User already exists"),
    Email_ALREADY_EXISTS("Email already exists"),
    Cin_ALREADY_EXISTS("Cin already exists"),
    Accounts_NOT_FOUND("Accounts not found for users"),

    DATE_ERROR("Invalid User");

    private final String message;

    UserErrMessage(String message) {
        this.message = message;
    }
}
