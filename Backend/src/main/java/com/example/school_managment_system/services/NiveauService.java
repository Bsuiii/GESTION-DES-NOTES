package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.NiveauDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Filiere;
import com.example.school_managment_system.models.Niveau;
import com.example.school_managment_system.repositories.FiliereRepository;
import com.example.school_managment_system.repositories.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NiveauService {

    @Autowired
    private NiveauRepository niveauRepository;

    @Autowired
    private FiliereRepository filiereRepository;

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
}