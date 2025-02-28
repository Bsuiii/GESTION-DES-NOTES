package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.FiliereDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Enseignant;
import com.example.school_managment_system.models.Filiere;
import com.example.school_managment_system.services.FilliereService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/filieres")
public class FilliereController {

    @Autowired
    private FilliereService filiereService;

    @Autowired
    private JWT jwtUtil;

    // Get all filieres
    @GetMapping
    public ResponseEntity<List<Filiere>> getAllFilieres(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(filiereService.getAllFilieres(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get filiere by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getFiliereById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            Filiere filiere = filiereService.getFiliereById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id " + id));
            return new ResponseEntity<>(filiere, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new filiere
    @PostMapping
    public ResponseEntity<Filiere> createFiliere(@RequestHeader("Authorization") String jwt, @RequestBody FiliereDto filieredto) throws Exception {
        System.err.println("Coordinateur ID :"+filieredto.getCoordonnateurId());
        return new ResponseEntity<>(filiereService.createFiliere(filieredto),jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing filiere
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFiliere(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody FiliereDto filieredto) throws Exception {
        System.err.println("Coordinateur ID :"+filieredto.getCoordonnateurId());
        try {
            Filiere updatedFiliere = filiereService.updateFiliere(id, filieredto);
            return new ResponseEntity<>(updatedFiliere, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a filiere
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFiliere(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            filiereService.deleteFiliere(id);
            return new ResponseEntity<>("Filiere deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}
