package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.ModuleDetailsDTO;
import com.example.school_managment_system.dto.ModuleDto;
import com.example.school_managment_system.dto.NoteDto;
import com.example.school_managment_system.dto.NotesCheckObject;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.*;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.repositories.*;
import com.example.school_managment_system.utils.Session;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModuleService {

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private ElementRepository elementRepository;
    // Get all modules
    public List<ModuleDto> getAllModules() {
        return moduleRepository.findAll().stream()
                .map(ModuleDto::toDto) // Use ModuleDto.toDto for conversion
                .collect(Collectors.toList());
    }

    // Get module by ID with filiere name
    public Optional<ModuleDto> getModuleById(int id) {
        return moduleRepository.findById(id)
                .map(ModuleDto::toDto); // Use ModuleDto.toDto for conversion
    }

    // Create a new module
    public ModuleDto createModule(ModuleDto moduleDto) throws ResourceNotFoundException {
        // Check if a module with the same code already exists
        if (moduleRepository.findByCode(moduleDto.getCode()).isPresent()) {
            throw new ResourceAlreadyExistsException("Module with code " + moduleDto.getCode() + " already exists.");
        }

        // Fetch the associated filiere
        Filiere filiere = filiereRepository.findById(moduleDto.getFiliere_id())
                .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id " + moduleDto.getFiliere_id()));

        // Fetch the associated filiere
        Niveau niveau = niveauRepository.findById(moduleDto.getNiveau_id())
                .orElseThrow(() -> new ResourceNotFoundException("Niveau not found with id " + moduleDto.getNiveau_id()));
        // Build and save the module
        Module module = moduleRepository.save(
                Module.builder()
                        .titre(moduleDto.getTitre())
                        .code(moduleDto.getCode())
                        .description(moduleDto.getDescription())
                        .filiere(filiere)
                        .niveau(niveau)
                        .build()
        );

        // Convert the saved module to ModuleDto before returning
        return ModuleDto.toDto(module);
    }

    // Update an existing module
    public ModuleDto updateModule(int id, ModuleDto moduleDetails) throws ResourceNotFoundException {
        Optional<Module> existingModule = moduleRepository.findById(id);
        if (existingModule.isPresent()) {
            Module updatedModule = existingModule.get();

            // Check if the new code conflicts with another module
            Optional<Module> moduleWithSameCode = moduleRepository.findByCode(moduleDetails.getCode());
            if (moduleWithSameCode.isPresent() && moduleWithSameCode.get().getId() != id) {
                throw new ResourceAlreadyExistsException("Module with code " + moduleDetails.getCode() + " already exists.");
            }

            // Fetch the associated filiere
            Filiere filiere = filiereRepository.findById(moduleDetails.getFiliere_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id " + moduleDetails.getFiliere_id()));

            Niveau niveau = niveauRepository.findById(moduleDetails.getNiveau_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Niveau not found with id " + moduleDetails.getNiveau_id()));
            // Update the module details
            updatedModule.setTitre(moduleDetails.getTitre());
            updatedModule.setCode(moduleDetails.getCode());
            updatedModule.setDescription(moduleDetails.getDescription());
            updatedModule.setNiveau(niveau);
            updatedModule.setFiliere(filiere);

            // Save the updated module and convert to ModuleDto before returning
            return ModuleDto.toDto(moduleRepository.save(updatedModule));
        } else {
            throw new ResourceNotFoundException("Module not found with id " + id);
        }
    }

    // Delete a module
    public void deleteModule(int id) throws ResourceNotFoundException {
        if (!moduleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Module not found with id " + id);
        }
        moduleRepository.deleteById(id);
    }



    public ModuleDetailsDTO getModuleDetails(int moduleId, Session session) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new RuntimeException("Module not found"));

        Filiere filiere = module.getFiliere();
        Enseignant coordonnateur = filiere.getCoordonnateur();
        Niveau niveau = module.getNiveau();

        String anneeUniversitaire = getCurrentAnneeUniversitaire();

        List<Inscription> inscriptions = inscriptionRepository.findByNiveau(niveau);
        List<ModuleDetailsDTO.StudentDetailsDTO> students = inscriptions.stream()
                .map(inscription -> {
                    Etudiant etudiant = inscription.getEtudiant();

                    // Initialize an empty list of elements for each student
                    List<ModuleDetailsDTO.ElementDetailsDTO> elements = module.getElements().stream()
                            .map(element -> ModuleDetailsDTO.ElementDetailsDTO.builder()
                                    .elementName(element.getTitre())
                                    .note(null)
                                    .moyenne(null)
                                    .validation(null)
                                    .build())
                            .collect(Collectors.toList());

                    return ModuleDetailsDTO.StudentDetailsDTO.builder()
                            .id(etudiant.getId())
                            .nom(etudiant.getNom())
                            .prenom(etudiant.getPrenom())
                            .cne(etudiant.getCne())
                            .elements(elements)
                            .build();
                })
                .collect(Collectors.toList());

        return ModuleDetailsDTO.builder()
                .moduleName(module.getTitre())
                .semester(module.getSemestre().toString())
                .anneeUniversitaire(anneeUniversitaire)
                .coordinateurNom(coordonnateur.getNom())
                .coordinateurPrenom(coordonnateur.getPrenom())
                .session(session.toString())
                .niveauAlias(niveau.getAlias())
                .students(students)
                .build();
    }

    private String getCurrentAnneeUniversitaire() {
        int currentYear = Year.now().getValue();
        return currentYear + "/" + (currentYear + 1);
    }

    private String calculateValidation(BigDecimal note, Filiere filiere, Niveau niveau) {
        int X = filiere.getX(); // Assuming X and Y are fields in Filiere
        int Y = filiere.getY();

        if (note.compareTo(BigDecimal.valueOf(X)) >= 0) {
            return "V";
        } else if (note.compareTo(BigDecimal.valueOf(Y)) >= 0) {
            return "R";
        } else {
            return "NV";
        }
    }

    // Path to the exports folder inside resources
    private static final String EXPORTS_FOLDER = "src/main/resources/exports/modules/";

    public String exportToExcel(ModuleDetailsDTO data,int moduleId) throws IOException {
        // Create workbook and sheet
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Module Info");

        // Create cell style for headers
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);

        // Create data cell style with borders
        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setBorderTop(BorderStyle.THIN);

        // Create the module info section (first two rows)
        // Row 1
        Row moduleRow1 = sheet.createRow(0);
        createStyledCell(moduleRow1, 0, "Module", headerStyle);
        createStyledCell(moduleRow1, 1, data.getModuleName(), dataCellStyle);
        createStyledCell(moduleRow1, 2, "Semestre", headerStyle);
        createStyledCell(moduleRow1, 3, data.getSemester(), dataCellStyle);
        createStyledCell(moduleRow1, 4, "Année", headerStyle);
        createStyledCell(moduleRow1, 5, data.getAnneeUniversitaire(), dataCellStyle);

        // Row 2
        Row moduleRow2 = sheet.createRow(1);
        createStyledCell(moduleRow2, 0, "Enseignant", headerStyle);
        createStyledCell(moduleRow2, 1, data.getCoordinateurNom() + " " + data.getCoordinateurPrenom(), dataCellStyle);
        createStyledCell(moduleRow2, 2, "Session", headerStyle);
        createStyledCell(moduleRow2, 3, data.getSession(), dataCellStyle);
        createStyledCell(moduleRow2, 4, "Classe", headerStyle);
        createStyledCell(moduleRow2, 5, data.getNiveauAlias(), dataCellStyle);

        // Empty row
        sheet.createRow(2);

        // Find all unique element names
        Set<String> elementNames = new LinkedHashSet<>();
        for (ModuleDetailsDTO.StudentDetailsDTO student : data.getStudents()) {
            for (ModuleDetailsDTO.ElementDetailsDTO element : student.getElements()) {
                elementNames.add(element.getElementName());
            }
        }
        List<String> elementList = new ArrayList<>(elementNames);

        // Create the grades header row
        Row gradesHeader = sheet.createRow(3);
        createStyledCell(gradesHeader, 0, "ID", headerStyle);
        createStyledCell(gradesHeader, 1, "CNE", headerStyle);
        createStyledCell(gradesHeader, 2, "NOM", headerStyle);
        createStyledCell(gradesHeader, 3, "PRENOM", headerStyle);

        // Add element columns
        int columnIndex = 4;
        int elementCount = elementList.size();
        for (String elementName : elementList) {
            createStyledCell(gradesHeader, columnIndex++, elementName, headerStyle);
        }

        // Add moyenne and validation columns
        createStyledCell(gradesHeader, columnIndex++, "Moyenne", headerStyle);
        createStyledCell(gradesHeader, columnIndex, "Validation", headerStyle);

        // Create formula cell style (red font)
        CellStyle formulaStyle = createFormulaStyle(workbook);

        // Fill in student data
        int rowNum = 4;

        for (ModuleDetailsDTO.StudentDetailsDTO student : data.getStudents()) {
            Row row = sheet.createRow(rowNum);

            // Student info
            createStyledCell(row, 0, String.valueOf(student.getId()), dataCellStyle);
            createStyledCell(row, 1, student.getCne(), dataCellStyle);
            createStyledCell(row, 2, student.getNom(), dataCellStyle);
            createStyledCell(row, 3, student.getPrenom(), dataCellStyle);

            // Create element grade cells (empty)
            columnIndex = 4;
            for (int i = 0; i < elementList.size(); i++) {
                Cell cell = row.createCell(columnIndex++);
                cell.setCellStyle(dataCellStyle);
                // Leave cell value empty for manual entry
            }

            // Add formula for moyenne (average of element grades)
            Cell moyenneCell = row.createCell(columnIndex++);
            moyenneCell.setCellStyle(formulaStyle);

            // Formula to calculate average of grade columns
            String moyenneFormula = "AVERAGE(" +
                    getCellReference(row.getRowNum(), 4) + ":" +
                    getCellReference(row.getRowNum(), 4 + elementCount - 1) + ")";
            moyenneCell.setCellFormula(moyenneFormula);

            // Add formula for validation based on moyenne
            Cell validationCell = row.createCell(columnIndex);
            validationCell.setCellStyle(formulaStyle);

            // Formula to determine validation status
            String validationFormula = "IF(" +
                    getCellReference(row.getRowNum(), columnIndex - 1) + ">=10,\"V\"," +
                    "IF(" + getCellReference(row.getRowNum(), columnIndex - 1) + ">=8,\"R\",\"NV\"))";
            validationCell.setCellFormula(validationFormula);

            rowNum++;
        }

        // Auto-size columns
        for (int i = 0; i < columnIndex + 1; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, Math.max(sheet.getColumnWidth(i), 4000)); // Minimum width of 4000
        }

        String fileName = "module_grades_" + data.getModuleName() + "_" + data.getSession() + "_" + System.currentTimeMillis() + ".xlsx";
        String filePath = Paths.get(EXPORTS_FOLDER + "/" + moduleId, fileName).toString();

        // Create the directory if it doesn't exist
        File directory = new File(Paths.get(EXPORTS_FOLDER, String.valueOf(moduleId)).toString());
        if (!directory.exists()) {
            try {
                Files.createDirectories(directory.toPath());
            } catch (IOException e) {
                throw new RuntimeException("Failed to create export directory", e);
            }
        }

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        } catch (IOException e) {
            throw new RuntimeException("Failed to export module details to Excel", e);
        }

        workbook.close();

        return filePath;
    }

    // Helper method to get cell reference (e.g., A5)
    private String getCellReference(int rowNum, int colNum) {
        return CellReference.convertNumToColString(colNum) + (rowNum + 1);
    }





    // Helper method to create a styled cell
    private void createStyledCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    // Helper method to create a formula cell style (red font)
    private CellStyle createFormulaStyle(Workbook workbook) {
        CellStyle formulaStyle = workbook.createCellStyle();
        formulaStyle.setBorderBottom(BorderStyle.THIN);
        formulaStyle.setBorderLeft(BorderStyle.THIN);
        formulaStyle.setBorderRight(BorderStyle.THIN);
        formulaStyle.setBorderTop(BorderStyle.THIN);

        Font font = workbook.createFont();
        font.setColor(IndexedColors.RED.getIndex());
        formulaStyle.setFont(font);
        return formulaStyle;
    }



    public List<NotesCheckObject> processNotesByModuleFile(int moduleId, Session session, MultipartFile file) {
        // Retrieve module details from the database
        ModuleDetailsDTO moduleDetails = getModuleDetails(moduleId, session);
        List<NotesCheckObject> notesCheckObjects =new ArrayList<>();
        // Parse the Excel file
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Validate the file first
            ValidateFile(sheet, moduleDetails, session);

            // Process notes for each student
            for (int i = 4; i <= sheet.getLastRowNum(); i++) { // Student data starts from row 5
                Row studentRow = sheet.getRow(i);

                // Get student ID
                String studentId = studentRow.getCell(0).getStringCellValue();
                int etudiantId = Integer.parseInt(studentId);

                // Get element names and process notes
                Row headerRow = sheet.getRow(3); // Header row with element names
                int elementStartColumn = 4; // Starting after "PRENOM"

                for (int j = elementStartColumn; j < headerRow.getLastCellNum() - 2; j++) {
                    // Get element name from header
                    String elementName = headerRow.getCell(j).getStringCellValue();

                    // Get note value
                    Cell noteCell = studentRow.getCell(j);
                    BigDecimal noteValue = BigDecimal.valueOf(noteCell.getNumericCellValue());

                    // Process the note
                    try {
                        notesCheckObjects.add(processStudentNote(etudiantId, elementName, moduleId, noteValue, session));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            return notesCheckObjects;

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

        public void ValidateFile(Sheet sheet, ModuleDetailsDTO moduleDetails, Session session){
            validateModuleInfo(sheet, moduleDetails, session);

            // Check teacher information
            validateTeacherInfo(sheet, moduleDetails, session);

            // Check student data
            validateStudentData(sheet, moduleDetails);

            // Validate elements in the header row
            validateElements(sheet, moduleDetails);

            // Check element notes for validity
            validateElementNotes(sheet, moduleDetails);

        }

        private void validateModuleInfo(Sheet sheet, ModuleDetailsDTO moduleDetails, Session session) {
            Row moduleInfoRow = sheet.getRow(0);
            if (!moduleInfoRow.getCell(0).getStringCellValue().equals("Module")) {
                throw new RuntimeException("La cellule A1 doit contenir 'Module'. Valeur trouvée : " + moduleInfoRow.getCell(0).getStringCellValue());
            }
            String moduleName = moduleInfoRow.getCell(1).getStringCellValue();
            if (!moduleName.equals(moduleDetails.getModuleName())) {
                throw new RuntimeException("La cellule B1 doit contenir '" + moduleDetails.getModuleName() + "'. Valeur trouvée : " + moduleName);
            }

            String semester = moduleInfoRow.getCell(3).getStringCellValue();
            if (!semester.equals(moduleDetails.getSemester())) {
                throw new RuntimeException("La cellule D1 doit contenir '" + moduleDetails.getSemester() + "'. Valeur trouvée : " + semester);
            }

            String academicYear = moduleInfoRow.getCell(5).getStringCellValue();
            if (!academicYear.equals(moduleDetails.getAnneeUniversitaire())) {
                throw new RuntimeException("La cellule F1 doit contenir '" + moduleDetails.getAnneeUniversitaire() + "'. Valeur trouvée : " + academicYear);
            }
        }

        private void validateTeacherInfo(Sheet sheet, ModuleDetailsDTO moduleDetails, Session session) {
            Row teacherInfoRow = sheet.getRow(1);
            if (!teacherInfoRow.getCell(0).getStringCellValue().equals("Enseignant")) {
                throw new RuntimeException("La cellule A2 doit contenir 'Enseignant'. Valeur trouvée : " + teacherInfoRow.getCell(0).getStringCellValue());
            }
            String teacherName = teacherInfoRow.getCell(1).getStringCellValue();
            String expectedTeacherName = moduleDetails.getCoordinateurNom() + " " + moduleDetails.getCoordinateurPrenom();
            if (!teacherName.equals(expectedTeacherName)) {
                throw new RuntimeException("La cellule B2 doit contenir '" + expectedTeacherName + "'. Valeur trouvée : " + teacherName);
            }

            String sessionInfo = teacherInfoRow.getCell(3).getStringCellValue();
            if (!sessionInfo.equals(session.toString())) {
                throw new RuntimeException("La cellule D2 doit contenir '" + session.toString() + "'. Valeur trouvée : " + sessionInfo);
            }

            String classInfo = teacherInfoRow.getCell(5).getStringCellValue();
            if (!classInfo.equals(moduleDetails.getNiveauAlias())) {
                throw new RuntimeException("La cellule F2 doit contenir '" + moduleDetails.getNiveauAlias() + "'. Valeur trouvée : " + classInfo);
            }
        }

    private void validateStudentData(Sheet sheet, ModuleDetailsDTO moduleDetails) {
        List<ModuleDetailsDTO.StudentDetailsDTO> students = moduleDetails.getStudents();
        for (int i = 4; i <= sheet.getLastRowNum(); i++) { // Student data starts from row 5
            Row studentRow = sheet.getRow(i);
            String studentId = studentRow.getCell(0).getStringCellValue();
            String studentCne = studentRow.getCell(1).getStringCellValue();
            String studentNom = studentRow.getCell(2).getStringCellValue();
            String studentPrenom = studentRow.getCell(3).getStringCellValue();

            ModuleDetailsDTO.StudentDetailsDTO student = students.stream()
                    .filter(s -> String.valueOf(s.getId()).equals(studentId))
                    .findFirst()
                    .orElse(null);

            if (student == null) {
                throw new RuntimeException("L'étudiant avec l'ID " + studentId + " (ligne " + (i + 1) + ") n'existe pas dans la base de données.");
            }
            if (!student.getCne().equals(studentCne)) {
                throw new RuntimeException("Le CNE de l'étudiant " + studentId + " (ligne " + (i + 1) + ") doit être '" + student.getCne() + "'. Valeur trouvée : " + studentCne);
            }
            if (!student.getNom().equals(studentNom)) {
                throw new RuntimeException("Le nom de l'étudiant " + studentId + " (ligne " + (i + 1) + ") doit être '" + student.getNom() + "'. Valeur trouvée : " + studentNom);
            }
            if (!student.getPrenom().equals(studentPrenom)) {
                throw new RuntimeException("Le prénom de l'étudiant " + studentId + " (ligne " + (i + 1) + ") doit être '" + student.getPrenom() + "'. Valeur trouvée : " + studentPrenom);
            }
        }
    }

    private void validateElements(Sheet sheet, ModuleDetailsDTO moduleDetails) {
        // Get the header row (row 4 in Excel, index 3 in Apache POI)
        Row headerRow = sheet.getRow(3);
        if (headerRow == null) {
            throw new RuntimeException("La ligne d'en-tête (ligne 4) est manquante dans le fichier Excel.");
        }

        // Extract element names from the header row (starting after "PRENOM" at column index 4)
        List<String> excelElementNames = new ArrayList<>();
        int elementStartColumn = 4; // Starting after "PRENOM"

        for (int j = elementStartColumn; j < headerRow.getLastCellNum()-2; j++) {
            Cell cell = headerRow.getCell(j);
            if (cell != null && cell.getCellType() == CellType.STRING) {
                excelElementNames.add(cell.getStringCellValue());
            } else {
                throw new RuntimeException("La cellule de l'en-tête de l'élément " + (j - elementStartColumn + 1) + " est vide ou invalide.");
            }
        }

        // Get the expected element names from the ModuleDetailsDTO
        List<String> expectedElementNames = moduleDetails.getStudents().get(0).getElements().stream()
                .map(ModuleDetailsDTO.ElementDetailsDTO::getElementName)
                .collect(Collectors.toList());

        // Compare the element names
        if (excelElementNames.size() != expectedElementNames.size()) {
            throw new RuntimeException("Le nombre d'éléments dans le fichier Excel (" + excelElementNames.size() + ") ne correspond pas au nombre attendu (" + expectedElementNames.size() + ").");
        }

        for (int j = 0; j < expectedElementNames.size(); j++) {
            String expectedElementName = expectedElementNames.get(j);
            String actualElementName = excelElementNames.get(j);

            if (!actualElementName.equals(expectedElementName)) {
                throw new RuntimeException("L'élément " + (j + 1) + " dans le fichier Excel doit être '" + expectedElementName + "'. Valeur trouvée : '" + actualElementName + "'.");
            }
        }
    }

    private void validateElementNotes(Sheet sheet, ModuleDetailsDTO moduleDetails) {
        List<ModuleDetailsDTO.StudentDetailsDTO> students = moduleDetails.getStudents();
        for (int i = 4; i <= sheet.getLastRowNum(); i++) { // Student data starts from row 5
            Row studentRow = sheet.getRow(i);
            if (studentRow == null) {
                throw new RuntimeException("La ligne " + (i + 1) + " est vide.");
            }

            String studentId = studentRow.getCell(0).getStringCellValue();

            ModuleDetailsDTO.StudentDetailsDTO student = students.stream()
                    .filter(s -> String.valueOf(s.getId()).equals(studentId))
                    .findFirst()
                    .orElse(null);

            if (student != null) {
                List<ModuleDetailsDTO.ElementDetailsDTO> elements = student.getElements();
                int elementStartColumn = 4; // Starting after "PRENOM"

                for (int j = 0; j < elements.size(); j++) {
                    Cell noteCell = studentRow.getCell(elementStartColumn + j);
                    if (noteCell == null || noteCell.getCellType() == CellType.BLANK) {
                        throw new RuntimeException("La note de l'élément " + (j + 1) + " de l'étudiant " + studentId + " (ligne " + (i + 1) + ") est manquante.");
                    }

                    if (noteCell.getCellType() != CellType.NUMERIC) {
                        throw new RuntimeException("La note de l'élément " + (j + 1) + " de l'étudiant " + studentId + " (ligne " + (i + 1) + ") doit être un nombre. Valeur trouvée : " + noteCell.toString());
                    }

                    double note = noteCell.getNumericCellValue();
                    if (note < 0.00 || note > 20.00) {
                        throw new RuntimeException("La note de l'élément " + (j + 1) + " de l'étudiant " + studentId + " (ligne " + (i + 1) + ") doit être entre 0.00 et 20.00. Valeur trouvée : " + note);
                    }
                }
            }
        }
    }

    public NotesCheckObject processStudentNote(int etudiantId, String elementName, int moduleId, BigDecimal newNoteValue, Session session) {
        // Find the element ID by its name and module ID
        Element element = elementRepository.findByTitreAndModuleId(elementName, moduleId)
                .orElseThrow(() -> new RuntimeException("Element not found: " + elementName + " in module ID: " + moduleId));

        int elementId = element.getId();

        // Check if the note already exists for the student and element
        Optional<Note> existingNote = noteRepository.findByEtudiantIdAndElementIdAndSession(etudiantId, elementId, session);

        if (existingNote.isPresent()) {
            // Convert existing note to DTO
            NoteDto currentNoteDto = NoteDto.toDto(existingNote.get());

            // Create a new NoteDto object with the updated value
            NoteDto newNoteDto = NoteDto.builder()
                    .etudiantId(etudiantId)
                    .elementId(elementId)
                    .session(session)
                    .note(newNoteValue)
                    .build();

            // Return NotesCheckObject for user confirmation
            return NotesCheckObject.builder()
                    .id(existingNote.get().getId())
                    .current_info(currentNoteDto)
                    .new_info(newNoteDto)
                    .build();
        } else {
            // No existing note found, create a new one
            Note newNote = new Note();
            newNote.setEtudiant(Etudiant.builder().id(etudiantId).build());
            newNote.setElement(Element.builder().id(elementId).build());
            newNote.setModule(Module.builder().id(moduleId).build());
            newNote.setSession(session);
            newNote.setCreatedAt(LocalDateTime.now());
            newNote.setNote(newNoteValue);

            noteRepository.save(newNote);

            return null;
        }
    }

}