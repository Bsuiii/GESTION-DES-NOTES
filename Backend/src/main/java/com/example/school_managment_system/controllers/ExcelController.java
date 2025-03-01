package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.InscriptionCheckObject;
import com.example.school_managment_system.dto.ModuleDetailsDTO;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.services.ExcelService;
import com.example.school_managment_system.services.ModuleService;
import com.example.school_managment_system.utils.Session;
import com.example.school_managment_system.utils.StudentComparison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_notes/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private ModuleService moduleService;


    @PostMapping("/upload")
    public List<InscriptionCheckObject> uploadExcelFile(@RequestParam("file") MultipartFile file) throws Exception {
        return excelService.processExcelFile(file);
    }

    @GetMapping("/is_next")
    public boolean isNext(@RequestParam("etudiant_id") int eid,@RequestParam("niv_id") int nivId) throws IOException {
        return excelService.isNiveauSuivant(eid,nivId);
    }

    @GetMapping("/{id}/detail")
    public ModuleDetailsDTO getModuleDetails(@PathVariable int id, @RequestParam String session) {
        return moduleService.getModuleDetails(id, Session.valueOf(session));
    }


    @GetMapping("/{id}/details")
    public ResponseEntity<String> exportToExcel(@PathVariable int id, @RequestParam String session) {
        ModuleDetailsDTO moduleDetails = moduleService.getModuleDetails(id, Session.valueOf(session));

        try {
            String filePath = moduleService.exportToExcel(moduleDetails);
            return ResponseEntity.ok("Excel file saved at: " + filePath);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Failed to export Excel file: " + e.getMessage());
        }
    }
}
