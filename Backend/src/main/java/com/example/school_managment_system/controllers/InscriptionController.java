package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.InscriptionDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.services.InscriptionService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/etudiant_inscriptions")
public class InscriptionController {

    @Autowired
    private InscriptionService InscriptionService;

    @Autowired
    private JWT jwtUtil;

    // Get all Inscriptions
    @GetMapping
    public ResponseEntity<List<InscriptionDto>> getAllInscriptions(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(InscriptionService.getAllInscriptions(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get Inscription by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getInscriptionById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            InscriptionDto Inscription = InscriptionService.getInscriptionById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Inscription not found with id " + id));
            return new ResponseEntity<>(Inscription, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new Inscription
    @PostMapping
    public ResponseEntity<InscriptionDto> createInscription(@RequestHeader("Authorization") String jwt, @RequestBody InscriptionDto InscriptionDto) throws Exception {
        return new ResponseEntity<>(InscriptionService.createInscription(InscriptionDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing Inscription
    @PutMapping("/{id}")
    public ResponseEntity<?> updateInscription(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody InscriptionDto InscriptionDto) throws Exception {
        try {
            com.example.school_managment_system.dto.InscriptionDto updatedInscription = InscriptionService.updateInscription(id, InscriptionDto);
            return new ResponseEntity<>(updatedInscription, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an Inscription
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteInscription(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            InscriptionService.deleteInscription(id);
            return new ResponseEntity<>("Inscription deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}