package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.NiveauDetailsDTO;
import com.example.school_managment_system.dto.NiveauDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.*;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.repositories.*;
import com.example.school_managment_system.utils.Session;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NiveauService {

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;
    // Get all niveaus
    public List<NiveauDto> getAllNiveaus() {
        return niveauRepository.findAll().stream()
                .map(NiveauDto::toDto)
                .collect(Collectors.toList());
    }

    // Get niveau by ID
    public Optional<NiveauDto> getNiveauById(int id) {
        return niveauRepository.findById(id)
                .map(NiveauDto::toDto);
    }

    // Create a new niveau
    public NiveauDto createNiveau(NiveauDto niveauDto) throws ResourceNotFoundException {
        // Check if a niveau with the same alias already exists
        if (niveauRepository.findByAlias(niveauDto.getAlias()).isPresent()) {
            throw new ResourceAlreadyExistsException("Niveau with alias " + niveauDto.getAlias() + " already exists.");
        }

        // Fetch the associated filiere
        Filiere filiere = filiereRepository.findById(niveauDto.getFiliere_id())
                .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id " + niveauDto.getFiliere_id()));

        // Fetch the associated niveauSuivant if it exists
        Niveau niveauSuivant = null;
        if (niveauDto.getNiveauSuivant_id() != null) {
            niveauSuivant = niveauRepository.findById(niveauDto.getNiveauSuivant_id())
                    .orElseThrow(() -> new ResourceNotFoundException("NiveauSuivant not found with id " + niveauDto.getNiveauSuivant_id()));
        }

        // Build and save the niveau
        Niveau niveau = niveauRepository.save(
                Niveau.builder()
                        .niveau(niveauDto.getNiveau())
                        .alias(niveauDto.getAlias())
                        .filiere(filiere)
                        .niveauSuivant(niveauSuivant)
                        .build()
        );

        // Convert the saved niveau to NiveauDto before returning
        return NiveauDto.toDto(niveau);
    }

    // Update an existing niveau
    public NiveauDto updateNiveau(int id, NiveauDto niveauDetails) throws ResourceNotFoundException {
        Optional<Niveau> existingNiveau = niveauRepository.findById(id);
        if (existingNiveau.isPresent()) {
            Niveau updatedNiveau = existingNiveau.get();

            // Check if the new alias conflicts with another niveau
            Optional<Niveau> niveauWithSameAlias = niveauRepository.findByAlias(niveauDetails.getAlias());
            if (niveauWithSameAlias.isPresent() && niveauWithSameAlias.get().getId() != id) {
                throw new ResourceAlreadyExistsException("Niveau with alias " + niveauDetails.getAlias() + " already exists.");
            }

            // Fetch the associated filiere
            Filiere filiere = filiereRepository.findById(niveauDetails.getFiliere_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Filiere not found with id " + niveauDetails.getFiliere_id()));

            // Fetch the associated niveauSuivant if it exists
            Niveau niveauSuivant = null;
            if (niveauDetails.getNiveauSuivant_id() != null) {
                niveauSuivant = niveauRepository.findById(niveauDetails.getNiveauSuivant_id())
                        .orElseThrow(() -> new ResourceNotFoundException("NiveauSuivant not found with id " + niveauDetails.getNiveauSuivant_id()));
            }

            // Update the niveau details
            updatedNiveau.setNiveau(niveauDetails.getNiveau());
            updatedNiveau.setAlias(niveauDetails.getAlias());
            updatedNiveau.setFiliere(filiere);
            updatedNiveau.setNiveauSuivant(niveauSuivant);

            // Save the updated niveau and convert to NiveauDto before returning
            return NiveauDto.toDto(niveauRepository.save(updatedNiveau));
        } else {
            throw new ResourceNotFoundException("Niveau not found with id " + id);
        }
    }

    // Delete a niveau
    public void deleteNiveau(int id) throws ResourceNotFoundException {
        if (!niveauRepository.existsById(id)) {
            throw new ResourceNotFoundException("Niveau not found with id " + id);
        }
        niveauRepository.deleteById(id);
    }



    /****************************** METHOD FOR RETURNING DELIBRATION *****************************/
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
    public NiveauDetailsDTO getNiveauDetails(int niveauId, Session session, LocalDate deliberationDate) {
        // Fetch the niveau and related data
        Niveau niveau = niveauRepository.findById(niveauId)
                .orElseThrow(() -> new RuntimeException("Niveau not found"));
        Filiere filiere = niveau.getFiliere();
        Enseignant coordonnateur = filiere.getCoordonnateur();
        String anneeUniversitaire = getCurrentAnneeUniversitaire();

        // Fetch modules for this niveau
        List<Module> modules = moduleRepository.findByNiveau(niveau);

        // Fetch all inscriptions for this niveau
        List<Inscription> inscriptions = inscriptionRepository.findByNiveau(niveau);

        // Create list of students with their module grades
        List<NiveauDetailsDTO.StudentDTO> students = inscriptions.stream()
                .map(inscription -> {
                    Etudiant etudiant = inscription.getEtudiant();

                    // Process each module for this student
                    List<NiveauDetailsDTO.ModuleResultDTO> moduleResults = modules.stream()
                            .map(module -> {
                                // Calculate module average and validation status
                                BigDecimal moduleAverage = calculateModuleAverage(etudiant.getId(), module.getId(), session);
                                String validation = calculateValidation(moduleAverage, filiere, niveau);

                                // Get element details for this module
                                List<NiveauDetailsDTO.ElementResultDTO> elementResults = module.getElements().stream()
                                        .map(element -> {
                                            BigDecimal note = getNoteForElement(etudiant.getId(), element.getId(), session);
                                            return NiveauDetailsDTO.ElementResultDTO.builder()
                                                    .elementId(element.getId())
                                                    .elementName(element.getTitre())
                                                    .note(note)
                                                    .build();
                                        })
                                        .collect(Collectors.toList());

                                return NiveauDetailsDTO.ModuleResultDTO.builder()
                                        .moduleId(module.getId())
                                        .moduleName(module.getTitre())
                                        .elements(elementResults)
                                        .moyenne(moduleAverage)
                                        .validation(validation)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    // Calculate overall average for student
                    BigDecimal overallAverage = calculateOverallAverage(moduleResults);

                    // Rank will be set later
                    Integer rank = null;

                    return NiveauDetailsDTO.StudentDTO.builder()
                            .id(etudiant.getId())
                            .cne(etudiant.getCne())
                            .nom(etudiant.getNom())
                            .prenom(etudiant.getPrenom())
                            .moduleResults(moduleResults)
                            .moyenne(overallAverage)
                            .rank(rank)
                            .build();
                })
                .collect(Collectors.toList());

        // Calculate and set student rankings based on overall averages
        calculateAndSetRankings(students);

        return NiveauDetailsDTO.builder()
                .niveauAlias(niveau.getAlias())
                .anneeUniversitaire(anneeUniversitaire)
                .deliberationDate(deliberationDate)
                .session(session.toString())
                .modules(modules.stream()
                        .map(module -> NiveauDetailsDTO.ModuleInfoDTO.builder()
                                .id(module.getId())
                                .name(module.getTitre())
                                .elements(module.getElements().stream()
                                        .map(element -> NiveauDetailsDTO.ElementInfoDTO.builder()
                                                .id(element.getId())
                                                .name(element.getTitre())
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .students(students)
                .build();
    }

    // Helper methods
    private BigDecimal calculateModuleAverage(int studentId, int moduleId, Session session) {
        // Logic to calculate module average based on element notes
        // This would typically involve fetching notes for all elements in the module
        // for the given student and session, then calculating the weighted average

        // Placeholder implementation
        return noteRepository.findModuleAverage(studentId, moduleId, session)
                .orElse(BigDecimal.ZERO);
    }

    private BigDecimal getNoteForElement(int studentId, int elementId, Session session) {
        // Fetch the note for a specific element, student, and session
        return noteRepository.findByEtudiantAndElementAndSession(studentId, elementId, session)
                .map(Note::getNote)
                .orElse(null);
    }

    private BigDecimal calculateOverallAverage(List<NiveauDetailsDTO.ModuleResultDTO> moduleResults) {
        // Calculate weighted average of module averages
        // This may involve module coefficients if they have different weights

        if (moduleResults.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal sum = moduleResults.stream()
                .filter(result -> result.getMoyenne() != null)
                .map(NiveauDetailsDTO.ModuleResultDTO::getMoyenne)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = moduleResults.stream()
                .filter(result -> result.getMoyenne() != null)
                .count();

        return count > 0 ? sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private void calculateAndSetRankings(List<NiveauDetailsDTO.StudentDTO> students) {
        // Sort students by average in descending order
        List<NiveauDetailsDTO.StudentDTO> sortedStudents = new ArrayList<>(students);
        sortedStudents.sort(Comparator.comparing(
                student -> student.getMoyenne() != null ? student.getMoyenne() : BigDecimal.ZERO,
                Comparator.reverseOrder()));

        // Assign ranks
        for (int i = 0; i < sortedStudents.size(); i++) {
            NiveauDetailsDTO.StudentDTO sortedStudent = sortedStudents.get(i);

            // Find this student in the original list and update rank
            for (NiveauDetailsDTO.StudentDTO student : students) {
                if (student.getId() == sortedStudent.getId()) {
                    student.setRank(i + 1);
                    break;
                }
            }
        }
    }


    /*********************** TO EXCEL MTHOD*********************/

    public Workbook generateNiveauDetailsExcel(NiveauDetailsDTO niveauDetails) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Relevé de notes");

        // Set column widths
        sheet.setColumnWidth(0, 3000); // ID column
        sheet.setColumnWidth(1, 3000); // CNE column
        sheet.setColumnWidth(2, 4000); // Last name column
        sheet.setColumnWidth(3, 4000); // First name column

        // Create cell styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle subHeaderStyle = createSubHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        CellStyle validationStyle = createValidationStyle(workbook);
        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle gradeStyle = createGradeStyle(workbook);

        // Calculate number of columns needed for all modules and their elements
        int totalColumns = 4; // Start with ID, CNE, NOM, PRENOM
        for (NiveauDetailsDTO.ModuleInfoDTO module : niveauDetails.getModules()) {
            // Each module needs columns for its elements, plus moyenne and validation columns
            totalColumns += module.getElements().size() + 2;
        }
        totalColumns += 2; // Add columns for overall average and rank

        // Create header rows
        // Row 1: Academic year and deliberation date
        Row yearRow = sheet.createRow(0);
        Cell yearLabelCell = yearRow.createCell(0);
        yearLabelCell.setCellValue("Année Universitaire");
        yearLabelCell.setCellStyle(titleStyle);

        Cell yearValueCell = yearRow.createCell(1);
        yearValueCell.setCellValue(niveauDetails.getAnneeUniversitaire());
        yearValueCell.setCellStyle(dataStyle);

        Cell dateLabelCell = yearRow.createCell(2);
        dateLabelCell.setCellValue("Date délibération");
        dateLabelCell.setCellStyle(titleStyle);

        Cell dateValueCell = yearRow.createCell(3);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        dateValueCell.setCellValue(niveauDetails.getDeliberationDate().format(formatter));
        dateValueCell.setCellStyle(dataStyle);

        // Row 2: Class information
        Row classRow = sheet.createRow(1);
        Cell classLabelCell = classRow.createCell(0);
        classLabelCell.setCellValue("Classe");
        classLabelCell.setCellStyle(titleStyle);

        Cell classValueCell = classRow.createCell(1);
        classValueCell.setCellValue(niveauDetails.getNiveauAlias());
        classValueCell.setCellStyle(dataStyle);

        // Row 3: Empty separator row
        Row separatorRow = sheet.createRow(2);
        for (int i = 0; i < totalColumns; i++) {
            Cell cell = separatorRow.createCell(i);
            cell.setCellStyle(subHeaderStyle);
        }

        // Row 4: Student ID headers
        Row studentHeaderRow = sheet.createRow(3);
        Cell idHeaderCell = studentHeaderRow.createCell(0);
        idHeaderCell.setCellValue("ID ETUDIANT");
        idHeaderCell.setCellStyle(headerStyle);

        Cell cneHeaderCell = studentHeaderRow.createCell(1);
        cneHeaderCell.setCellValue("CNE");
        cneHeaderCell.setCellStyle(headerStyle);

        Cell nomHeaderCell = studentHeaderRow.createCell(2);
        nomHeaderCell.setCellValue("NOM");
        nomHeaderCell.setCellStyle(headerStyle);

        Cell prenomHeaderCell = studentHeaderRow.createCell(3);
        prenomHeaderCell.setCellValue("PRENOM");
        prenomHeaderCell.setCellStyle(headerStyle);

        // Create module headers
        int columnIndex = 4;
        Map<Integer, List<Integer>> moduleColumnMap = new HashMap<>();

        for (NiveauDetailsDTO.ModuleInfoDTO module : niveauDetails.getModules()) {
            int startColumn = columnIndex;
            int elementCount = module.getElements().size();

            // Create merged cell for module name
            Cell moduleCell = studentHeaderRow.createCell(columnIndex);
            moduleCell.setCellValue("Module " + module.getName());
            moduleCell.setCellStyle(headerStyle);

            sheet.addMergedRegion(new CellRangeAddress(
                    3, // first row (0-based)
                    3, // last row  (0-based)
                    columnIndex, // first column (0-based)
                    columnIndex + elementCount + 1  // last column (0-based) - includes elements + moyenne + validation
            ));

            // Increment column index for next module
            columnIndex += elementCount + 2;

            // Store module column positions for later use
            List<Integer> columns = new ArrayList<>();
            columns.add(startColumn);
            columns.add(startColumn + elementCount + 1);
            moduleColumnMap.put(module.getId(), columns);
        }

        // Add moyenne and rang headers
        Cell moyenneHeaderCell = studentHeaderRow.createCell(columnIndex);
        moyenneHeaderCell.setCellValue("Moyenne");
        moyenneHeaderCell.setCellStyle(headerStyle);

        Cell rangHeaderCell = studentHeaderRow.createCell(columnIndex + 1);
        rangHeaderCell.setCellValue("Rang");
        rangHeaderCell.setCellStyle(headerStyle);

        // Row 5: Element headers and moyenne/validation sub-headers
        Row elementHeaderRow = sheet.createRow(4);
        columnIndex = 4;

        for (NiveauDetailsDTO.ModuleInfoDTO module : niveauDetails.getModules()) {
            // Add element headers
            for (NiveauDetailsDTO.ElementInfoDTO element : module.getElements()) {
                Cell elementCell = elementHeaderRow.createCell(columnIndex);
                elementCell.setCellValue("Élément " + element.getName());
                elementCell.setCellStyle(subHeaderStyle);
                columnIndex++;
            }

            // Add moyenne and validation headers
            Cell moyenneCell = elementHeaderRow.createCell(columnIndex);
            moyenneCell.setCellValue("Moyenne");
            moyenneCell.setCellStyle(subHeaderStyle);
            columnIndex++;

            Cell validationCell = elementHeaderRow.createCell(columnIndex);
            validationCell.setCellValue("Validation");
            validationCell.setCellStyle(subHeaderStyle);
            columnIndex++;
        }

        // Empty style row
        Row emptyStyleRow = sheet.createRow(5);

        // Add student data
        int rowIndex = 6;
        for (NiveauDetailsDTO.StudentDTO student : niveauDetails.getStudents()) {
            Row studentRow = sheet.createRow(rowIndex);

            // Add student information
            Cell idCell = studentRow.createCell(0);
            idCell.setCellValue(student.getId());
            idCell.setCellStyle(dataStyle);

            Cell cneCell = studentRow.createCell(1);
            cneCell.setCellValue(student.getCne());
            cneCell.setCellStyle(dataStyle);

            Cell nomCell = studentRow.createCell(2);
            nomCell.setCellValue(student.getNom());
            nomCell.setCellStyle(dataStyle);

            Cell prenomCell = studentRow.createCell(3);
            prenomCell.setCellValue(student.getPrenom());
            prenomCell.setCellStyle(dataStyle);

            // Add module data
            columnIndex = 4;
            for (NiveauDetailsDTO.ModuleResultDTO moduleResult : student.getModuleResults()) {
                // Add element notes
                for (NiveauDetailsDTO.ElementResultDTO elementResult : moduleResult.getElements()) {
                    Cell noteCell = studentRow.createCell(columnIndex);
                    if (elementResult.getNote() != null) {
                        noteCell.setCellValue(elementResult.getNote().doubleValue());
                    }
                    noteCell.setCellStyle(gradeStyle);
                    columnIndex++;
                }

                // Add module average
                Cell moduleAvgCell = studentRow.createCell(columnIndex);
                if (moduleResult.getMoyenne() != null) {
                    moduleAvgCell.setCellValue(moduleResult.getMoyenne().doubleValue());
                }
                moduleAvgCell.setCellStyle(gradeStyle);
                columnIndex++;

                // Add validation status
                Cell validCell = studentRow.createCell(columnIndex);
                validCell.setCellValue(moduleResult.getValidation());
                validCell.setCellStyle(validationStyle);
                columnIndex++;
            }

            // Add overall average
            Cell overallAvgCell = studentRow.createCell(columnIndex);
            if (student.getMoyenne() != null) {
                overallAvgCell.setCellValue(student.getMoyenne().doubleValue());
            }
            overallAvgCell.setCellStyle(gradeStyle);

            // Add rank
            Cell rankCell = studentRow.createCell(columnIndex + 1);
            if (student.getRank() != null) {
                rankCell.setCellValue(student.getRank());
            }
            rankCell.setCellStyle(dataStyle);

            rowIndex++;
        }

        return workbook;
    }

    /**
     * Create header style - bold, light green background with borders
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Create sub-header style - bold with borders
     */
    private CellStyle createSubHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Create data style - standard with borders
     */
    private CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Create validation style - bold and centered with borders
     */
    private CellStyle createValidationStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Create title style for row headers
     */
    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Create grade style with number format
     */
    private CellStyle createGradeStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setDataFormat(workbook.createDataFormat().getFormat("0.0"));
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
}