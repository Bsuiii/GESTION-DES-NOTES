package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.EtudiantDto;
import com.example.school_managment_system.dto.InscriptionCheckObject;
import com.example.school_managment_system.dto.InscriptionDto;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Inscription;
import com.example.school_managment_system.models.Niveau;
import com.example.school_managment_system.repositories.EtudiantRepository;
import com.example.school_managment_system.repositories.InscriptionRepository;
import com.example.school_managment_system.repositories.NiveauRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;

@Service
public class ExcelService {
    private final EtudiantRepository etudiantRepository;
    private final NiveauRepository niveauRepository;
    private final InscriptionRepository inscriptionRepository;

    @Autowired
    private EtudiantService etudiantService;

    @Autowired
    private InscriptionService inscriptionService;

    public ExcelService(EtudiantRepository etudiantRepository, NiveauRepository niveauRepository, InscriptionRepository inscriptionRepository) {
        this.etudiantRepository = etudiantRepository;
        this.niveauRepository = niveauRepository;
        this.inscriptionRepository = inscriptionRepository;
    }

    @Transactional
    public List<InscriptionCheckObject> processExcelFile(MultipartFile file) throws Exception {
        List<Inscription> inscriptions = new ArrayList<>();
        List<Etudiant> etudiants = new ArrayList<>();
        List<InscriptionCheckObject> checkObjects=new ArrayList<>();
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            validateFileFormat(sheet);
            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                processRow(row, etudiants, inscriptions,checkObjects);
            }
        }
        System.err.println(
                " Student List:"+etudiants
        );
        for(Etudiant etudiant:etudiants)
            etudiantService.createEtudiant(EtudiantDto.toDto(etudiant));


        for(Inscription insc:inscriptions){
            System.err.println("insc "+InscriptionDto.toDto(insc));
            inscriptionService.createInscription(InscriptionDto.toDto(insc));
        }


        return checkObjects;
    }

    private void validateFileFormat(Sheet sheet) {
        String[] expectedHeaders = {"ID ETUDIANT", "CNE", "NOM", "PRENOM", "ID NIVEAU ACTUEL", "Type"};
        Row headerRow = sheet.getRow(0);
        if (headerRow == null || headerRow.getPhysicalNumberOfCells() != expectedHeaders.length) {
            throw new RuntimeException("Le fichier Excel doit avoir exactement " + expectedHeaders.length + " colonnes.");
        }
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!expectedHeaders[i].equals(headerRow.getCell(i).getStringCellValue().trim())) {
                throw new RuntimeException("La colonne " + (i + 1) + " doit être '" + expectedHeaders[i] + "'.");
            }
        }
    }

    private void processRow(Row row, List<Etudiant> etudiants, List<Inscription> inscriptions,List<InscriptionCheckObject> checkObjects) {
        int idEtudiant = (int) row.getCell(0).getNumericCellValue();
        String cne = row.getCell(1).getStringCellValue();
        String nom = row.getCell(2).getStringCellValue();
        String prenom = row.getCell(3).getStringCellValue();
        int idNiveau = (int) row.getCell(4).getNumericCellValue();
        String type = row.getCell(5).getStringCellValue();

        Niveau niveau = niveauRepository.findById(idNiveau).orElseThrow(() ->
                new RuntimeException("Le niveau avec l'ID " + idNiveau + " n'existe pas."));
        Optional<Etudiant> existingEtudiant = etudiantRepository.findById(idEtudiant);

        InscriptionCheckObject checkObject = checkForChanges(existingEtudiant, idEtudiant, cne, nom, prenom);
        if(checkObject != null){
            checkObjects.add(checkObject);
        }


        if ("REINSCRIPTION".equalsIgnoreCase(type)) {
            handleReinscription(existingEtudiant, idEtudiant, niveau, inscriptions);
        } else if ("INSCRIPTION".equalsIgnoreCase(type)) {
            handleInscription(existingEtudiant, idEtudiant, cne, nom, prenom, niveau, etudiants, inscriptions);
        } else {
            throw new RuntimeException("Type d'inscription invalide pour l'étudiant avec l'ID " + idEtudiant + ".");
        }
    }

    private void handleReinscription(Optional<Etudiant> existingEtudiant, int idEtudiant, Niveau niveau, List<Inscription> inscriptions) {
        if (existingEtudiant.isPresent()) {
            if (!isNiveauSuivant(idEtudiant, niveau.getId())) {
                throw new RuntimeException("L'étudiant avec l'ID " + idEtudiant + " ne peut pas être réinscrit dans ce niveau.");
            }
            inscriptions.add(createInscription(existingEtudiant.get(), niveau));
        } else {
            throw new RuntimeException("L'étudiant avec l'ID " + idEtudiant + " n'existe pas pour une réinscription.");
        }
    }

    private InscriptionCheckObject checkForChanges(Optional<Etudiant> existingEtudiant, int idEtudiant, String cne, String nom, String prenom) {
        if (existingEtudiant.isPresent()) {
            Etudiant etudiant = existingEtudiant.get();
            EtudiantDto currentInfo = new EtudiantDto(etudiant.getId(), etudiant.getCne(), etudiant.getNom(), etudiant.getPrenom());
            EtudiantDto newInfo = new EtudiantDto(idEtudiant, cne, nom, prenom);

            if (!etudiant.getCne().equals(cne) || !etudiant.getNom().equals(nom) || !etudiant.getPrenom().equals(prenom)) {
                return new InscriptionCheckObject(currentInfo, newInfo, newInfo, null);
            }
        }
        return null;
    }
    private void handleInscription(Optional<Etudiant> existingEtudiant, int idEtudiant, String cne, String nom, String prenom, Niveau niveau, List<Etudiant> etudiants, List<Inscription> inscriptions) {
        if (existingEtudiant.isPresent()) {
            throw new RuntimeException("L'étudiant avec l'ID " + idEtudiant + " existe déjà pour une inscription.");
        }

        Etudiant newEtudiant = Etudiant.builder()
                .id(idEtudiant)
                .cne(cne)
                .nom(nom)
                .prenom(prenom)
                .build();
        System.err.println(
                "NEW STUDENT :"+newEtudiant
        );
        etudiants.add(newEtudiant);
        System.err.println(
                "Add NEW ETUDIANT TO ETUDIANTS LIST"
        );
        inscriptions.add(createInscription(newEtudiant, niveau));
        System.err.println(
                "ADD INSCRIPTION FOR STUDENT"
        );
    }

    private Inscription createInscription(Etudiant etudiant, Niveau niveau) {
        System.err.println("YEAR :"+String.valueOf(LocalDate.now().getYear()));
        return Inscription.builder()
                .etudiant(etudiant)
                .niveau(niveau)
                .debutAnneUniversitaire(String.valueOf(LocalDate.now().getYear()))
                .finAnneUniversitaire(String.valueOf(LocalDate.now().getYear() + 1))
                .build();

    }

    public boolean isNiveauSuivant(int etudiantId, Integer niveauId) {
        // Fetch the last niveau_id directly from the database
        Optional<Integer> lastNiveauIdOpt = inscriptionRepository.findLastNiveauIdByEtudiantId(etudiantId);

        if (lastNiveauIdOpt.isEmpty()) {
            return false;
        }

        int lastNiveauId = lastNiveauIdOpt.get();

        // Fetch the next niveau_id instead of full entity
        Optional<Integer> nextNiveauIdOpt = niveauRepository.findNiveauSuivantId(lastNiveauId);

        return (nextNiveauIdOpt.get() == niveauId);
    }
}
