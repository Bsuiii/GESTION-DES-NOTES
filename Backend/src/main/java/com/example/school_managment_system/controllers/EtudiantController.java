package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.EtudiantDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.services.EtudiantService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private JWT jwtUtil;

    // Get all etudiants
    @GetMapping
    public ResponseEntity<List<EtudiantDto>> getAllEtudiants(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(etudiantService.getAllEtudiants(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get etudiant by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEtudiantById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            EtudiantDto etudiant = etudiantService.getEtudiantById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id " + id));
            return new ResponseEntity<>(etudiant, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new etudiant
    @PostMapping
    public ResponseEntity<EtudiantDto> createEtudiant(@RequestHeader("Authorization") String jwt, @RequestBody EtudiantDto etudiantDto) throws Exception {
        return new ResponseEntity<>(etudiantService.createEtudiant(etudiantDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    @PostMapping("/change_info")
    public ResponseEntity<List<EtudiantDto>> createEtudiant(@RequestHeader("Authorization") String jwt, @RequestBody List<EtudiantDto> etudiantDto) throws Exception {
        return new ResponseEntity<>(etudiantService.updateEtudiants(etudiantDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing etudiant
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEtudiant(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody EtudiantDto etudiantDto) throws Exception {
        try {
            EtudiantDto updatedEtudiant = etudiantService.updateEtudiant(id, etudiantDto);
            return new ResponseEntity<>(updatedEtudiant, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an etudiant
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEtudiant(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            etudiantService.deleteEtudiant(id);
            return new ResponseEntity<>("Etudiant deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}