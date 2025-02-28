package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.ModuleDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.services.ModuleService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/modules")
public class ModuleController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private JWT jwtUtil;

    // Get all modules
    @GetMapping
    public ResponseEntity<List<ModuleDto>> getAllModules(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(moduleService.getAllModules(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get module by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getModuleById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            ModuleDto module = moduleService.getModuleById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + id));
            return new ResponseEntity<>(module, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new module
    @PostMapping
    public ResponseEntity<ModuleDto> createModule(@RequestHeader("Authorization") String jwt, @RequestBody ModuleDto moduleDto) throws Exception {
        return new ResponseEntity<>(moduleService.createModule(moduleDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing module
    @PutMapping("/{id}")
    public ResponseEntity<?> updateModule(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody ModuleDto moduleDto) throws Exception {
        try {
            ModuleDto updatedModule = moduleService.updateModule(id, moduleDto);
            return new ResponseEntity<>(updatedModule, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a module
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteModule(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            moduleService.deleteModule(id);
            return new ResponseEntity<>("Module deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}