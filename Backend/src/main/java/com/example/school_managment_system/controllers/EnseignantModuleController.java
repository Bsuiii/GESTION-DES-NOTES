package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.EnseignantModuleDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.services.EnseignantModuleService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/enseignant_modules")
public class EnseignantModuleController {

    @Autowired
    private EnseignantModuleService enseignantModuleService;

    @Autowired
    private JWT jwtUtil;

    // Get all enseignantModules
    @GetMapping
    public ResponseEntity<List<EnseignantModuleDto>> getAllEnseignantModules(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(enseignantModuleService.getAllEnseignantModules(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get enseignantModule by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getEnseignantModuleById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            EnseignantModuleDto enseignantModule = enseignantModuleService.getEnseignantModuleById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("EnseignantModule not found with id " + id));
            return new ResponseEntity<>(enseignantModule, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new enseignantModule
    @PostMapping
    public ResponseEntity<EnseignantModuleDto> createEnseignantModule(@RequestHeader("Authorization") String jwt, @RequestBody EnseignantModuleDto enseignantModuleDto) throws Exception {
        return new ResponseEntity<>(enseignantModuleService.createEnseignantModule(enseignantModuleDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing enseignantModule
    @PutMapping("/{id}")
    public ResponseEntity<?> updateEnseignantModule(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody EnseignantModuleDto enseignantModuleDto) throws Exception {
        try {
            EnseignantModuleDto updatedEnseignantModule = enseignantModuleService.updateEnseignantModule(id, enseignantModuleDto);
            return new ResponseEntity<>(updatedEnseignantModule, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an enseignantModule
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEnseignantModule(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            enseignantModuleService.deleteEnseignantModule(id);
            return new ResponseEntity<>("EnseignantModule deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}