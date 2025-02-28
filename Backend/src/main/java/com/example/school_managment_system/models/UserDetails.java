package com.example.school_managment_system.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class UserDetails {
    private int userId;
    private String firstname;
    private String lastname;
    private String cin;
    private String tel;
    private String email;
    private String user_Role;
    private String login;
    private String password;
    private boolean is_locked;
    private boolean is_active;


}
