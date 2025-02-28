package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.UserDto;
import com.example.school_managment_system.exceptions.accountsException.AccountErrMsg;
import com.example.school_managment_system.exceptions.accountsException.AccountException;
import com.example.school_managment_system.exceptions.usersExceptions.UserErrMessage;
import com.example.school_managment_system.exceptions.usersExceptions.UserException;
import com.example.school_managment_system.models.Account;
import com.example.school_managment_system.models.User;
import com.example.school_managment_system.models.UserDetails;
import com.example.school_managment_system.repositories.AccountRepository;
import com.example.school_managment_system.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    // Create User
    public User createUser(User user) throws UserException{
        if(userRepository.existsByEmail(user.getEmail()))
            throw new UserException(UserErrMessage.Email_ALREADY_EXISTS);
        if(userRepository.existsByCin(user.getCin()))
            throw new UserException(UserErrMessage.Cin_ALREADY_EXISTS);
        return userRepository.save(user);
    }

    // Get All Users
    public List<UserDto> list_users() {
        List<User> users = userRepository.findAll();
        UserDto userDto=new UserDto();
        try {
            return userDto.users_To_Dto(users);
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return Collections.emptyList();
        }
    }

    // Get User by ID
    public UserDto getUserById(int id) throws UserException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserException(UserErrMessage.User_NOT_FOUND));

        return new UserDto().user_To_Dto(user);
    }


    // Update User
    public User updateUser(int id, User userDetails) {
        User user = userRepository.findById(id).get();
        user.setFirstname(userDetails.getFirstname());
        user.setLastname(userDetails.getLastname());
        user.setCin(userDetails.getCin());
        user.setTel(userDetails.getTel());
        user.setEmail(userDetails.getEmail());
        return userRepository.save(user);
    }

    // Delete User
    public void deleteUser(int id) {
        User user = userRepository.findById(id).get();
        userRepository.delete(user);
    }

    // Login User
    public UserDetails loginUser(String email, String password) throws AccountException{
        Account account = accountRepository.findByLoginAndPassword(email, password);
        if (account == null) {
            throw new AccountException(AccountErrMsg.INVALID_CREDENTIALS);
        }
        return UserDetails.builder()
                .userId(account.getUser().getId())
                .firstname(account.getUser().getFirstname())
                .lastname(account.getUser().getLastname())
                .cin(account.getUser().getCin())
                .tel(account.getUser().getTel())
                .email(account.getUser().getEmail())
                .user_Role(account.getRole().getCode())
                .login(account.getLogin())
                .password(account.getPassword())
                .is_locked(account.is_locked())
                .is_active(account.is_active())
                .build();
    }
}
