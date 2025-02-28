package com.example.school_managment_system.controllers;

import com.example.school_managment_system.models.Account;
import com.example.school_managment_system.models.Credentials;
import com.example.school_managment_system.models.UserDetails;
import com.example.school_managment_system.repositories.AccountRepository;
import com.example.school_managment_system.services.UsersService;
import com.example.school_managment_system.utils.JWT;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/")
@RequiredArgsConstructor
public class MainController {

    @Autowired
    private UsersService usersService;

    private final JWT jwt;
    @GetMapping("/home")
    public String  testFunction(){
     return "Hello World";
    }

    @GetMapping("/error")
    public String  errorFunction(){
        return "Error Occured";
    }
    @PostMapping("/auth/login")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> login_test(@RequestBody Credentials user) throws Exception {
        UserDetails userDetails=usersService.loginUser(user.getEmail(), user.getPassword());
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt.generateToken(userDetails));
        return new ResponseEntity<>(userDetails,headers, HttpStatus.OK);
    }


}

