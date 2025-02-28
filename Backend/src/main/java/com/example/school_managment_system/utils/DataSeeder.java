package com.example.school_managment_system.utils;


import com.example.school_managment_system.models.Account;
import com.example.school_managment_system.models.Role;
import com.example.school_managment_system.models.User;
import com.example.school_managment_system.repositories.AccountRepository;
import com.example.school_managment_system.repositories.RoleRepository;
import com.example.school_managment_system.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(UserRepository userRepository, RoleRepository roleRepository, AccountRepository accountRepository) {
        return args -> {
            if (roleRepository.count() == 0) {
                Role admin_note = Role.builder().name("Admin des notes").code("ADMIN_NOTES").description(" Accède uniquement à la gestion des notes et des étudiants").build();
                Role admin_sp = Role.builder().name("Admin Sp").code("ADMIN_SP").description(" Accède uniquement à la gestion des structures pédagogiques").build();
                Role admin_users = Role.builder().name("Admin users").code("ADMIN_USER").description("Accède uniquement à la gestion des comptes").build();
                roleRepository.saveAll(List.of(admin_note, admin_sp,admin_users));
            }

            if (userRepository.count() == 0) {
                User user1 = User.builder()
                        .firstname("John")
                        .lastname("Doe")
                        .cin("AB123456")
                        .tel("123456789")
                        .email("john.doe@example.com")
                        .build();

                User user2 = User.builder()
                        .firstname("Jane")
                        .lastname("Smith")
                        .cin("CD789012")
                        .tel("987654321")
                        .email("jane.smith@example.com")
                        .build();

                userRepository.saveAll(List.of(user1, user2));
            }

            if (accountRepository.count() == 0) {
                Role userRole = roleRepository.findByCode("ADMIN_USER").orElseThrow();
                Role adminRole = roleRepository.findByCode("ADMIN_NOTES").orElseThrow();
                Role admin_sp = roleRepository.findByCode("ADMIN_SP").orElseThrow();
                User user1 = userRepository.findByEmail("john.doe@example.com").orElseThrow();
                User user2 = userRepository.findByEmail("jane.smith@example.com").orElseThrow();

                Account account1 = Account.builder()
                        .login("admin")
                        .password("admin123")
                        .is_active(true)
                        .is_locked(false)
                        .user(user1)
                        .role(adminRole)
                        .build();

                Account account2 = Account.builder()
                        .login("jane")
                        .password("password")
                        .is_active(true)
                        .is_locked(false)
                        .user(user2)
                        .role(userRole)
                        .build();

                accountRepository.saveAll(List.of(account1, account2));
            }
        };
    }
}
