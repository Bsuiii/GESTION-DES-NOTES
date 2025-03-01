package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.ModuleDetailsDTO;
import com.example.school_managment_system.dto.ModuleDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.*;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.repositories.*;
import com.example.school_managment_system.utils.Session;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Paths;
import java.time.Year;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private ExcelService excelService;



    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private InscriptionRepository inscriptionRepository;
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
                    List<Note> notes = noteRepository.findByEtudiantAndModuleAndSession(etudiant, module, session);

                    // Group notes by element and calculate moyenne for each element
                    Map<Element, List<Note>> notesByElement = notes.stream()
                            .collect(Collectors.groupingBy(Note::getElement));

                    List<ModuleDetailsDTO.ElementDetailsDTO> elements = notesByElement.entrySet().stream()
                            .map(entry -> {
                                Element element = entry.getKey();
                                List<Note> elementNotes = entry.getValue();

                                // Calculate moyenne for the element
                                BigDecimal moyenne = elementNotes.stream()
                                        .map(Note::getNote)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                                        .divide(BigDecimal.valueOf(elementNotes.size()), 2, RoundingMode.HALF_UP);

                                // Get the latest note for the element (or any note, depending on your logic)
                                BigDecimal latestNote = elementNotes.get(0).getNote();

                                // Calculate validation
                                String validation = calculateValidation(latestNote, filiere, niveau);

                                return ModuleDetailsDTO.ElementDetailsDTO.builder()
                                        .elementName(element.getTitre())
                                        .note(latestNote)
                                        .moyenne(moyenne)
                                        .validation(validation)
                                        .build();
                            })
                            .collect(Collectors.toList());

                    return ModuleDetailsDTO.StudentDetailsDTO.builder()
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
    private static final String EXPORTS_FOLDER = "src/main/resources/exports/";

    public String exportToExcel(ModuleDetailsDTO data) throws IOException {
        // Create workbook and sheets
        Workbook workbook = new XSSFWorkbook();
        Sheet moduleInfoSheet = workbook.createSheet("Module Info");
        Sheet gradesSheet = workbook.createSheet("Grades");

        // Write module info
        Row moduleInfoHeader = moduleInfoSheet.createRow(0);
        moduleInfoHeader.createCell(0).setCellValue("Module");
        moduleInfoHeader.createCell(1).setCellValue("Semestre");
        moduleInfoHeader.createCell(2).setCellValue("Année Universitaire");
        moduleInfoHeader.createCell(3).setCellValue("Coordinateur");
        moduleInfoHeader.createCell(4).setCellValue("Session");
        moduleInfoHeader.createCell(5).setCellValue("Classe");

        Row moduleInfoRow = moduleInfoSheet.createRow(1);
        moduleInfoRow.createCell(0).setCellValue(data.getModuleName());
        moduleInfoRow.createCell(1).setCellValue(data.getSemester());
        moduleInfoRow.createCell(2).setCellValue(data.getAnneeUniversitaire());
        moduleInfoRow.createCell(3).setCellValue(data.getCoordinateurNom() + " " + data.getCoordinateurPrenom());
        moduleInfoRow.createCell(4).setCellValue(data.getSession());
        moduleInfoRow.createCell(5).setCellValue(data.getNiveauAlias());

        // Write student grades
        Row gradesHeader = gradesSheet.createRow(0);
        gradesHeader.createCell(0).setCellValue("ID");
        gradesHeader.createCell(1).setCellValue("CNE");
        gradesHeader.createCell(2).setCellValue("NOM");
        gradesHeader.createCell(3).setCellValue("PRENOM");
        gradesHeader.createCell(4).setCellValue("Elément");
        gradesHeader.createCell(5).setCellValue("Note");
        gradesHeader.createCell(6).setCellValue("Moyenne");
        gradesHeader.createCell(7).setCellValue("Validation");

        int rowNum = 1;
        for (ModuleDetailsDTO.StudentDetailsDTO student : data.getStudents()) {
            for (ModuleDetailsDTO.ElementDetailsDTO element : student.getElements()) {
                Row row = gradesSheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getCne());
                row.createCell(1).setCellValue(student.getCne());
                row.createCell(2).setCellValue(student.getNom());
                row.createCell(3).setCellValue(student.getPrenom());
                row.createCell(4).setCellValue(element.getElementName());
                row.createCell(5).setCellValue(element.getNote().doubleValue());
                row.createCell(6).setCellValue(element.getMoyenne().doubleValue());
                row.createCell(7).setCellValue(element.getValidation());
            }
        }

        // Define the file name and path
        String fileName = "module_grades_" + System.currentTimeMillis() + ".xlsx";
        String filePath = Paths.get(EXPORTS_FOLDER, fileName).toString();

        // Write to file
        try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
            workbook.write(fileOut);
        }

        workbook.close();

        return filePath; // Return the file path for reference
    }
}