package com.example.school_managment_system.controllers;
import java.util.Random;

import com.example.school_managment_system.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;


@RestController
@RequestMapping("/api/email/")
public class EmailController {

    @Autowired
    private EmailService emailService;

    private int codeVerification;


    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestParam String email) throws MessagingException {
        try {
            int random = 1000 + new Random().nextInt(9000);
            codeVerification = random;
            emailService.sendEmail(email, "Code De Verification", "Le Code De Verification est: " + random);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Unexpected Error: " + e.getMessage());
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/VerifierCode")

    public ResponseEntity<?> verifyCode(@RequestParam int code) {
        return new ResponseEntity<>(code == codeVerification, HttpStatus.OK);
    }
}
