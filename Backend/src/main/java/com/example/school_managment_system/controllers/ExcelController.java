package com.example.school_managment_system.controllers;

import com.example.school_managment_system.dto.InscriptionCheckObject;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.services.ExcelService;
import com.example.school_managment_system.utils.StudentComparison;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api/admin_notes/excel")
public class ExcelController {

    @Autowired
    private ExcelService excelService;

    @PostMapping("/upload")
    public List<InscriptionCheckObject> uploadExcelFile(@RequestParam("file") MultipartFile file) throws Exception {
        return excelService.processExcelFile(file);
    }

    @GetMapping("/is_next")
    public boolean isNext(@RequestParam("etudiant_id") int eid,@RequestParam("niv_id") int nivId) throws IOException {
        return excelService.isNiveauSuivant(eid,nivId);
    }
}
