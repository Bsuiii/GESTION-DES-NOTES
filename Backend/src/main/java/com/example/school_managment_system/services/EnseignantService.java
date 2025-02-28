package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.EnseignantDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Enseignant;
import com.example.school_managment_system.models.Filiere;
import com.example.school_managment_system.models.EnseignantModule;
import com.example.school_managment_system.repositories.EnseignantRepository;
import com.example.school_managment_system.repositories.FiliereRepository;
import com.example.school_managment_system.repositories.EnseignantModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnseignantService {

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private EnseignantModuleRepository enseignantModuleRepository;

    // Get all enseignants
    public List<EnseignantDto> getAllEnseignants() {
        return enseignantRepository.findAll().stream()
                .map(EnseignantDto::toDto)
                .collect(Collectors.toList());
    }

    // Get enseignant by ID
    public Optional<EnseignantDto> getEnseignantById(int id) {
        return enseignantRepository.findById(id)
                .map(EnseignantDto::toDto);
    }

    // Create a new enseignant
    public EnseignantDto createEnseignant(EnseignantDto enseignantDto) throws ResourceNotFoundException {
        // Check if an enseignant with the same email already exists
        if (enseignantRepository.findByEmail(enseignantDto.getEmail()).isPresent()) {
            throw new ResourceAlreadyExistsException("Enseignant with email " + enseignantDto.getEmail() + " already exists.");
        }

        // Build and save the enseignant
        Enseignant enseignant = enseignantRepository.save(
                Enseignant.builder()
                        .nom(enseignantDto.getNom())
                        .prenom(enseignantDto.getPrenom())
                        .email(enseignantDto.getEmail())
                        .build()
        );

        // Convert the saved enseignant to EnseignantDto before returning
        return EnseignantDto.toDto(enseignant);
    }

    // Update an existing enseignant
    public EnseignantDto updateEnseignant(int id, EnseignantDto enseignantDetails) throws ResourceNotFoundException {
        Optional<Enseignant> existingEnseignant = enseignantRepository.findById(id);
        if (existingEnseignant.isPresent()) {
            Enseignant updatedEnseignant = existingEnseignant.get();

            // Check if the new email conflicts with another enseignant
            Optional<Enseignant> enseignantWithSameEmail = enseignantRepository.findByEmail(enseignantDetails.getEmail());
            if (enseignantWithSameEmail.isPresent() && enseignantWithSameEmail.get().getId() != id) {
                throw new ResourceAlreadyExistsException("Enseignant with email " + enseignantDetails.getEmail() + " already exists.");
            }

            // Update the enseignant details
            updatedEnseignant.setNom(enseignantDetails.getNom());
            updatedEnseignant.setPrenom(enseignantDetails.getPrenom());
            updatedEnseignant.setEmail(enseignantDetails.getEmail());

            // Save the updated enseignant and convert to EnseignantDto before returning
            return EnseignantDto.toDto(enseignantRepository.save(updatedEnseignant));
        } else {
            throw new ResourceNotFoundException("Enseignant not found with id " + id);
        }
    }

    // Delete an enseignant
    public void deleteEnseignant(int id) throws ResourceNotFoundException {
        if (!enseignantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enseignant not found with id " + id);
        }
        enseignantRepository.deleteById(id);
    }
}