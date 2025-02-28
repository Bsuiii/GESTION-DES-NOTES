package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Account;
import com.example.school_managment_system.models.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private int id;
    private String firstname;
    private String lastname;
    private String cin;
    private String tel;
    private String email;
    private List<Account> accounts;
    public UserDto user_To_Dto(User user) {
        try {
                List<Account> accounts = new ArrayList<>();

                for (Account acc : user.getAccounts()) {
                    accounts.add(Account.builder()
                            .id(acc.getId())
                            .login(acc.getLogin())
                            .password(acc.getPassword())
                            .role(acc.getRole())
                            .is_locked(acc.is_locked())
                            .is_active(acc.is_active())
                            .build());
                }
                return UserDto.builder()
                                .id(user.getId())
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .email(user.getEmail())
                                .cin(user.getCin())
                                .tel(user.getTel())
                                 .accounts(accounts)
                                .build();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    public List<UserDto> users_To_Dto(List<User> users) {
        List<UserDto> usersDto = new ArrayList<>();
        try {
            for (User user : users) {
                List<Account> accounts = new ArrayList<>();

                for (Account acc : user.getAccounts()) {
                    accounts.add(Account.builder()
                            .id(acc.getId())
                            .login(acc.getLogin())
                            .password(acc.getPassword())
                            .role(acc.getRole())
                            .is_locked(acc.is_locked())
                            .is_active(acc.is_active())
                            .build());
                }

                usersDto.add(
                        UserDto.builder()
                                .id(user.getId()) // Add this line to set the id field
                                .firstname(user.getFirstname())
                                .lastname(user.getLastname())
                                .email(user.getEmail())
                                .cin(user.getCin())
                                .tel(user.getTel())
                                .accounts(accounts)
                                .build()
                );
            }
            return usersDto;
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            return Collections.emptyList();
        }
    }
}
