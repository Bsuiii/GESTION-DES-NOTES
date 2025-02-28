package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.EtudiantDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.repositories.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EtudiantService {

    @Autowired
    private EtudiantRepository etudiantRepository;

    // Get all etudiants
    public List<EtudiantDto> getAllEtudiants() {
        return etudiantRepository.findAll().stream()
                .map(EtudiantDto::toDto)
                .collect(Collectors.toList());
    }

    // Get etudiant by ID
    public Optional<EtudiantDto> getEtudiantById(int id) {
        return etudiantRepository.findById(id)
                .map(EtudiantDto::toDto);
    }

    // Create a new etudiant
    public EtudiantDto createEtudiant(EtudiantDto etudiantDto) throws ResourceNotFoundException {
        // Check if an etudiant with the same CNE already exists
        if (etudiantRepository.findByCne(etudiantDto.getCne()).isPresent()) {
            throw new ResourceAlreadyExistsException("Etudiant with CNE " + etudiantDto.getCne() + " already exists.");
        }
        // Build and save the etudiant
        Etudiant etudiant = etudiantRepository.save(
                Etudiant.builder()
                        .id((etudiantDto.getId() != 0) ? etudiantDto.getId() : etudiantRepository.findMaxId()+1)
                        .nom(etudiantDto.getNom())
                        .prenom(etudiantDto.getPrenom())
                        .cne(etudiantDto.getCne())
                        .build()
        );

        // Convert the saved etudiant to EtudiantDto before returning
        return EtudiantDto.toDto(etudiant);
    }

    // Update an existing etudiant
    public EtudiantDto updateEtudiant(int id, EtudiantDto etudiantDetails) throws ResourceNotFoundException {
        Optional<Etudiant> existingEtudiant = etudiantRepository.findById(id);
        if (existingEtudiant.isPresent()) {
            Etudiant updatedEtudiant = existingEtudiant.get();

            // Check if the new CNE conflicts with another etudiant
            Optional<Etudiant> etudiantWithSameCne = etudiantRepository.findByCne(etudiantDetails.getCne());
            if (etudiantWithSameCne.isPresent() && etudiantWithSameCne.get().getId() != id) {
                throw new ResourceAlreadyExistsException("Etudiant with CNE " + etudiantDetails.getCne() + " already exists.");
            }


            // Update the etudiant details
            updatedEtudiant.setNom(etudiantDetails.getNom());
            updatedEtudiant.setPrenom(etudiantDetails.getPrenom());
            updatedEtudiant.setCne(etudiantDetails.getCne());

            // Save the updated etudiant and convert to EtudiantDto before returning
            return EtudiantDto.toDto(etudiantRepository.save(updatedEtudiant));
        } else {
            throw new ResourceNotFoundException("Etudiant not found with id " + id);
        }
    }

    public List<EtudiantDto> updateEtudiants(List<EtudiantDto> etudiants) throws Exception {
       List<EtudiantDto> etudiantDto = new ArrayList<>();
        for(EtudiantDto etud:etudiants){
            etudiantDto.add(
                    EtudiantDto.toDto(etudiantRepository.save(
                                                                Etudiant.builder()
                                                                        .id(etud.getId())
                                                                        .cne(etud.getCne())
                                                                        .nom(etud.getNom())
                                                                        .prenom(etud.getPrenom())
                                                                        .build()
                                                               )
                        ));
        }
    return etudiantDto;
    }

    // Delete an etudiant
    public void deleteEtudiant(int id) throws ResourceNotFoundException {
        if (!etudiantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etudiant not found with id " + id);
        }
        etudiantRepository.deleteById(id);
    }
}