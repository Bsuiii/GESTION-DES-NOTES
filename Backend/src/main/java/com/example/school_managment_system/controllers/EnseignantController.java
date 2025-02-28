package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.EnseignantDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.services.EnseignantService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/enseignants")
public class EnseignantController {

    @Autowired
    private EnseignantService enseignantService;

    @Autowired
    private JWT jwtUtil;

    // Get all enseignants
    @GetMapping
    public ResponseEntity<List<EnseignantDto>> getAllEnseignants(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(enseignantService.getAllEnseignants(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get enseignant by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEnseignantById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            EnseignantDto enseignant = enseignantService.getEnseignantById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant not found with id " + id));
            return new ResponseEntity<>(enseignant, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new enseignant
    @PostMapping
    public ResponseEntity<EnseignantDto> createEnseignant(@RequestHeader("Authorization") String jwt, @RequestBody EnseignantDto enseignantDto) throws Exception {
        return new ResponseEntity<>(enseignantService.createEnseignant(enseignantDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing enseignant
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnseignant(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody EnseignantDto enseignantDto) throws Exception {
        try {
            EnseignantDto updatedEnseignant = enseignantService.updateEnseignant(id, enseignantDto);
            return new ResponseEntity<>(updatedEnseignant, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an enseignant
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnseignant(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            enseignantService.deleteEnseignant(id);
            return new ResponseEntity<>("Enseignant deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}