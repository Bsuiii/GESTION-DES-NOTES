package com.example.school_managment_system.controllers;

import com.example.school_managment_system.services.ZipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/zip/export")
public class ZipController {

    @Autowired
    private ZipService zipService;

    @GetMapping("/niveau/{niveauId}")
    public ResponseEntity<InputStreamResource> exportNiveauFiles(@PathVariable int niveauId) throws IOException {
        File zipFile = zipService.exportExcelFilesAsZip(niveauId);

        // Prepare the file for download
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + zipFile.getName())
                .body(resource);
    }
}
