package com.example.school_managment_system.repositories;

import com.example.school_managment_system.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Integer> {

    Account findByLoginAndPassword(String email,String Password);
    boolean existsByLogin(String login);
    Optional<Account> findByLogin(String login);
}
