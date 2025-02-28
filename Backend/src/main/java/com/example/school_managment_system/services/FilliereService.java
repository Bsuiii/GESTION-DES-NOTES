package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.FiliereDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Enseignant;
import com.example.school_managment_system.models.Filiere;
import com.example.school_managment_system.repositories.FiliereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FilliereService {

    @Autowired
    private FiliereRepository filiereRepository;

    // Get all filieres
    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }

    // Get filiere by ID
    public Optional<Filiere> getFiliereById(int id) {
        return filiereRepository.findById(id);
    }

    // Create a new filiere
    public Filiere createFiliere(FiliereDto filiereDto) {
        // Check if a filiere with the same alias already exists
        if (filiereRepository.findByAlias(filiereDto.getAlias()).isPresent()) {
            throw new ResourceAlreadyExistsException("Filiere with alias " + filiereDto.getAlias() + " already exists.");
        }
        return filiereRepository.save(
                Filiere.builder()
                        .alias(filiereDto.getAlias())
                        .anneeAccreditation(filiereDto.getAnneeAccreditation())
                        .anneeFinAccreditation(filiereDto.getAnneeFinAccreditation())
                        .intitule(filiereDto.getIntitule())
                        .coordonnateur(
                                Enseignant.builder()
                                        .id(filiereDto.getCoordonnateurId())
                                        .build())
                        .build()
        );
    }

    // Update an existing filiere
    public Filiere updateFiliere(int id, FiliereDto filiereDetails) throws ResourceNotFoundException {
        Optional<Filiere> existingFiliere = filiereRepository.findById(id);
        if (existingFiliere.isPresent()) {
            Filiere updatedFiliere = existingFiliere.get();
            // Check if the new alias conflicts with another filiere
            Optional<Filiere> filiereWithSameAlias = filiereRepository.findByAlias(filiereDetails.getAlias());
            if (filiereWithSameAlias.isPresent() && filiereWithSameAlias.get().getId() != id) {
                throw new ResourceAlreadyExistsException("Filiere with alias " + filiereDetails.getAlias() + " already exists.");
            }
            updatedFiliere.setAlias(filiereDetails.getAlias());
            updatedFiliere.setIntitule(filiereDetails.getIntitule());
            updatedFiliere.setAnneeAccreditation(filiereDetails.getAnneeAccreditation());
            updatedFiliere.setAnneeFinAccreditation(filiereDetails.getAnneeFinAccreditation());
            updatedFiliere.setCoordonnateur(
                    Enseignant.builder()
                            .id(filiereDetails.getCoordonnateurId())
                            .build()
            );
            return filiereRepository.save(updatedFiliere);
        } else {
            throw new ResourceNotFoundException("Filiere not found with id " + id);
        }
    }

    // Delete a filiere
    public void deleteFiliere(int id) throws ResourceNotFoundException {
        if (!filiereRepository.existsById(id)) {
            throw new ResourceNotFoundException("Filiere not found with id " + id);
        }
        filiereRepository.deleteById(id);
    }
}