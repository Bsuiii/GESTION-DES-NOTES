package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.NiveauDetailsDTO;
import com.example.school_managment_system.dto.NiveauDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.services.NiveauService;
import com.example.school_managment_system.utils.JWT;
import com.example.school_managment_system.utils.Session;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_sp/niveaux")
public class NiveauController {

    @Autowired
    private NiveauService niveauService;

    @Autowired
    private JWT jwtUtil;

    // Get all niveaus
    @GetMapping
    public ResponseEntity<List<NiveauDto>> getAllNiveaus(@RequestHeader("Authorization") String jwt) throws Exception {
        return new ResponseEntity<>(niveauService.getAllNiveaus(), jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
    }

    // Get niveau by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getNiveauById(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            NiveauDto niveau = niveauService.getNiveauById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Niveau not found with id " + id));
            return new ResponseEntity<>(niveau, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }

    // Create a new niveau
    @PostMapping
    public ResponseEntity<NiveauDto> createNiveau(@RequestHeader("Authorization") String jwt, @RequestBody NiveauDto niveauDto) throws Exception {
        return new ResponseEntity<>(niveauService.createNiveau(niveauDto), jwtUtil.checkTheJWT(jwt), HttpStatus.CREATED);
    }

    // Update an existing niveau
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNiveau(@RequestHeader("Authorization") String jwt, @PathVariable int id, @RequestBody NiveauDto niveauDto) throws Exception {
        try {
            NiveauDto updatedNiveau = niveauService.updateNiveau(id, niveauDto);
            return new ResponseEntity<>(updatedNiveau, jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException | ResourceAlreadyExistsException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.BAD_REQUEST);
        }
    }

    // Delete a niveau
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNiveau(@RequestHeader("Authorization") String jwt, @PathVariable int id) throws Exception {
        try {
            niveauService.deleteNiveau(id);
            return new ResponseEntity<>("Niveau deleted successfully", jwtUtil.checkTheJWT(jwt), HttpStatus.OK);
        } catch (ResourceNotFoundException ex) {
            return new ResponseEntity<>(ex.getMessage(), jwtUtil.checkTheJWT(jwt), HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/excel/{niveauId}/details")
    public ResponseEntity<NiveauDetailsDTO> getNiveauDetails(
            @PathVariable int niveauId,
            @RequestParam Session session,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deliberationDate) {

        // If deliberation date is not provided, use current date
        if (deliberationDate == null) {
            deliberationDate = LocalDate.now();
        }

        NiveauDetailsDTO details = niveauService.getNiveauDetails(niveauId, session, deliberationDate);
        return ResponseEntity.ok(details);
    }


    @GetMapping("/excel/{niveauId}/export")
    public ResponseEntity<byte[]> exportNiveauDetails(
            @PathVariable int niveauId,
            @RequestParam Session session,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate deliberationDate) {

        // If deliberation date is not provided, use current date
        if (deliberationDate == null) {
            deliberationDate = LocalDate.now();
        }

        try {
            // Get the niveau details
            NiveauDetailsDTO niveauDetails = niveauService.getNiveauDetails(niveauId, session, deliberationDate);

            // Generate Excel workbook
            Workbook workbook = niveauService.generateNiveauDetailsExcel(niveauDetails);

            // Convert workbook to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            // Set up response headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
            String filename = "releve_notes_" + niveauDetails.getNiveauAlias() + "_" +
                    niveauDetails.getAnneeUniversitaire().replace("/", "_") + ".xlsx";
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(outputStream.toByteArray(), headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}