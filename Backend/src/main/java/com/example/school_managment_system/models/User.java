package com.example.school_managment_system.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    private String firstname;
    private String lastname;
    private String cin;
    private String tel;
    private String email;

    @OneToMany(mappedBy="user",cascade=  CascadeType.ALL  )
    private List<Account> accounts;
}
