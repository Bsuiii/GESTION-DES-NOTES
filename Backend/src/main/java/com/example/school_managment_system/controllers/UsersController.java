package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.UserDto;
import com.example.school_managment_system.exceptions.accountsException.AccountException;
import com.example.school_managment_system.exceptions.usersExceptions.UserException;
import com.example.school_managment_system.models.Credentials;
import com.example.school_managment_system.models.User;
import com.example.school_managment_system.models.UserDetails;
import com.example.school_managment_system.services.UsersService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_users")
public class UsersController {

    @Autowired
    private UsersService userService;

    @Autowired
    private JWT jwtUtil;
    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(userService.list_users(),jwtUtil.checkTheJWT(jwt),HttpStatus.OK);
    }

    // Get user by ID
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String jwt,@PathVariable int id) throws Exception {
        try {
            return new ResponseEntity<>(userService.getUserById(id),jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (UserException e) {
            return new ResponseEntity<>(e.getMessage(),jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new user
    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestHeader("Authorization") String jwt,@RequestBody User user) throws Exception {
        return new ResponseEntity<>(userService.createUser(user),jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update user
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String jwt,@PathVariable int id, @RequestBody User userDetails) throws Exception {
        try {
            return new ResponseEntity<>(userService.updateUser(id, userDetails), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found", jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Delete user
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String jwt,@PathVariable int id) throws Exception {
        try {
            userService.deleteUser(id);
            return new ResponseEntity<>("User deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("User not found",jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Login user
  /*  @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Credentials credentials) {
        try {
            UserDetails userDetails = userService.loginUser(credentials.getEmail(), credentials.getPassword());
            return new ResponseEntity<>(userDetails, HttpStatus.OK);
        } catch (AccountException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }*/
}
