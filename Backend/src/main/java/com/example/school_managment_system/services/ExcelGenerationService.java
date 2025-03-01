package com.example.school_managment_system.services;

import com.example.school_managment_system.models.*;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.repositories.ElementRepository;
import com.example.school_managment_system.repositories.EtudiantRepository;
import com.example.school_managment_system.repositories.ModuleRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ExcelGenerationService {
/*
        @Autowired
        private ModuleRepository moduleRepository;

        @Autowired
        private EtudiantRepository etudiantRepository;

        @Autowired
        private ElementRepository elementRepository;

        public void generateModuleGradesExcel(int moduleId, String sessionType) throws IOException {
            // Fetch the module, students, and elements from the database
            Module module = moduleRepository.findById(moduleId)
                    .orElseThrow(() -> new RuntimeException("Module not found with id: " + moduleId));

            List<Etudiant> students = etudiantRepository.findByModule(module);
            List<Element> elements = elementRepository.findByModule(module);

            // Create a new workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Grades");

            // Create styles for headers and data
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Module", module.getTitre(), "Semestre", module.getSemestre().toString(), "Année",
                    module.getNiveau().getInscriptions().getLast().getDebutAnneUniversitaire() + "/" +
                            module.getNiveau().getInscriptions().getLast().getFinAnneUniversitaire(), "", ""};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create teacher and session row
            Row teacherRow = sheet.createRow(1);
            String[] teacherInfo = {"Enseignant", module.getEnseignantModules().getLast().getEnseignant().getNom() + " " +
                    module.getEnseignantModules().getLast().getEnseignant().getPrenom(), "Session", sessionType.toUpperCase(), "Classe",
                    module.getNiveau().getAlias(), "", ""};
            for (int i = 0; i < teacherInfo.length; i++) {
                Cell cell = teacherRow.createCell(i);
                cell.setCellValue(teacherInfo[i]);
                cell.setCellStyle(headerStyle);
            }

            // Create student data header row
            Row studentHeaderRow = sheet.createRow(3);
            String[] studentHeaders = {"ID", "CNE", "NOM", "PRENOM"};
            for (Element element : elements) {
                studentHeaders = Arrays.copyOf(studentHeaders, studentHeaders.length + 1);
                studentHeaders[studentHeaders.length - 1] = element.getTitre();
            }
            studentHeaders = Arrays.copyOf(studentHeaders, studentHeaders.length + 2);
            studentHeaders[studentHeaders.length - 2] = "Moyenne";
            studentHeaders[studentHeaders.length - 1] = "Validation";

            for (int i = 0; i < studentHeaders.length; i++) {
                Cell cell = studentHeaderRow.createCell(i);
                cell.setCellValue(studentHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate student data
            int rowIndex = 4;
            for (Etudiant student : students) {
                Row studentRow = sheet.createRow(rowIndex++);
                studentRow.createCell(0).setCellValue(student.getId());
                studentRow.createCell(1).setCellValue(student.getCne());
                studentRow.createCell(2).setCellValue(student.getNom());
                studentRow.createCell(3).setCellValue(student.getPrenom());

                // Add grades for each element
                for (int i = 0; i < elements.size(); i++) {
                    Cell gradeCell = studentRow.createCell(4 + i);
                    gradeCell.setCellStyle(dataStyle);
                    // Here you would fetch the grade for the student and element from the database
                    // For now, we leave it blank
                    gradeCell.setCellValue("");
                }

                // Add formula for average
                Cell averageCell = studentRow.createCell(4 + elements.size());
                averageCell.setCellStyle(dataStyle);
                String averageFormula = "AVERAGE(E" + (rowIndex) + ":" + (char) ('D' + elements.size() + 1) + (rowIndex) + ")";
                averageCell.setCellFormula(averageFormula);

                // Add formula for validation
                Cell validationCell = studentRow.createCell(4 + elements.size() + 1);
                validationCell.setCellStyle(dataStyle);
                String validationFormula = "IF(" + (char) ('D' + elements.size() + 2) + (rowIndex) + ">=" + module.getX() + ",\"V\",IF(" +
                        (char) ('D' + elements.size() + 2) + (rowIndex) + ">=" + module.getY() + ",\"R\",\"NV\"))";
                validationCell.setCellFormula(validationFormula);
            }

            // Auto-size columns
            for (int i = 0; i < studentHeaders.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            try (FileOutputStream fileOut = new FileOutputStream("Module_Grades_" + module.getCode() + "_" + sessionType.toUpperCase() + ".xlsx")) {
                workbook.write(fileOut);
            }

            // Close the workbook
            workbook.close();
        }
*/
}