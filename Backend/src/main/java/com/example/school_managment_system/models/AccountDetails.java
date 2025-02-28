package com.example.school_managment_system.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetails {
    private int role_id;
    private int user_id;
    private String login;
    private String password;
    private boolean is_active;

}
