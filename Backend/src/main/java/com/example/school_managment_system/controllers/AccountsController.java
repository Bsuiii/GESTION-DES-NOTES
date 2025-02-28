package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.AccountDto;
import com.example.school_managment_system.exceptions.accountsException.AccountException;
import com.example.school_managment_system.models.Account;
import com.example.school_managment_system.services.AccountService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_accounts")
public class AccountsController {

    @Autowired
    private AccountService accountsService;

    @Autowired
    private JWT jwtUtil;

    // Get all accounts
    @GetMapping("/accounts")
    public ResponseEntity<List<AccountDto>> getAllAccounts(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(accountsService.listAccounts(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get account by ID
    @GetMapping("/accounts/{id}")
    public ResponseEntity<?> getAccountById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            return new ResponseEntity<>(accountsService.getAccountById(id), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (AccountException e) {
            return new ResponseEntity<>(e.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new account
    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestHeader("Authorization") String jwt, @RequestBody Account account) throws Exception {
        return new ResponseEntity<>(accountsService.createAccount(account), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update account
    @PutMapping("/accounts/{id}")
    public ResponseEntity<?> updateAccount(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody Account accountDetails) throws Exception {
        try {
            return new ResponseEntity<>(accountsService.updateAccount(id, accountDetails), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (AccountException e) {
            return new ResponseEntity<>(e.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Delete account
    @DeleteMapping("/accounts/{id}")
    public ResponseEntity<?> deleteAccount(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            accountsService.deleteAccount(id);
            return new ResponseEntity<>("Account deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (AccountException e) {
            return new ResponseEntity<>(e.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}