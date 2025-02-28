package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.ElementDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.services.ElementService;
import com.example.school_managment_system.utils.JWT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/elements")
public class ElementController {

    @Autowired
    private ElementService elementService;

    @Autowired
    private JWT jwtUtil;

    // Get all elements
    @GetMapping
    public ResponseEntity<List<ElementDto>> getAllElements(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(elementService.getAllElements(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get element by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getElementById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            ElementDto element = elementService.getElementById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Element not found with id " + id));
            return new ResponseEntity<>(element, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new element
    @PostMapping
    public ResponseEntity<ElementDto> createElement(@RequestHeader("Authorization") String jwt, @RequestBody ElementDto elementDto) throws Exception {
        return new ResponseEntity<>(elementService.createElement(elementDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing element
    @PutMapping("/{id}")
    public ResponseEntity<?> updateElement(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody ElementDto elementDto) throws Exception {
        try {
            ElementDto updatedElement = elementService.updateElement(id, elementDto);
            return new ResponseEntity<>(updatedElement, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete an element
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteElement(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            elementService.deleteElement(id);
            return new ResponseEntity<>("Element deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }
}