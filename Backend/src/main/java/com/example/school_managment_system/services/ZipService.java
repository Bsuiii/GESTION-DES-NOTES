package com.example.school_managment_system.services;

import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.models.Niveau;
import com.example.school_managment_system.repositories.ModuleRepository;
import com.example.school_managment_system.repositories.NiveauRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipService {

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    // This method will take the niveauId, find all associated modules,
    // gather their Excel files, and return a zip file for download.
    public File exportExcelFilesAsZip(int niveauId) throws IOException {
        // Step 1: Find the Niveau by ID
        Niveau niveau = niveauRepository.findById(niveauId).orElseThrow(() -> new FileNotFoundException("Niveau not found"));

        // Step 2: Get the list of modules for this niveau
        List<Module> modules = niveau.getModules();

        // Step 3: Prepare a temporary file for the zip
        File tempZipFile = new File(System.getProperty("java.io.tmpdir") + "/niveau_" + niveauId + "_export.zip");
        try (FileOutputStream fos = new FileOutputStream(tempZipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            // Step 4: Loop over each module and gather all Excel files
            for (Module module : modules) {
                String moduleFolderPath = "src/main/resources/exports/modules/" + module.getId();

                // Get all files in the module folder (assuming Excel files only)
                File moduleFolder = new File(moduleFolderPath);
                if (moduleFolder.exists() && moduleFolder.isDirectory()) {
                    File[] excelFiles = moduleFolder.listFiles((dir, name) -> name.endsWith(".xlsx") || name.endsWith(".xls"));
                    if (excelFiles != null) {
                        for (File excelFile : excelFiles) {
                            // Step 5: Add each file to the zip
                            try (FileInputStream fis = new FileInputStream(excelFile)) {
                                ZipEntry zipEntry = new ZipEntry(module.getCode() + "/" + excelFile.getName());
                                zipOut.putNextEntry(zipEntry);
                                byte[] bytes = new byte[1024];
                                int length;
                                while ((length = fis.read(bytes)) >= 0) {
                                    zipOut.write(bytes, 0, length);
                                }
                                zipOut.closeEntry();
                            }
                        }
                    }
                }
            }
        }

        // Return the created zip file
        return tempZipFile;
    }
}
