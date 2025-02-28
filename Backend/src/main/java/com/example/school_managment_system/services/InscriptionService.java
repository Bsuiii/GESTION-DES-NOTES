package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.InscriptionDto;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Etudiant;
import com.example.school_managment_system.models.Inscription;
import com.example.school_managment_system.models.Niveau;
import com.example.school_managment_system.repositories.InscriptionRepository;
import com.example.school_managment_system.repositories.EtudiantRepository;
import com.example.school_managment_system.repositories.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InscriptionService {

    @Autowired
    private InscriptionRepository inscriptionRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private NiveauRepository niveauRepository;

    // Get all Inscriptions
    public List<InscriptionDto> getAllInscriptions() {
        return inscriptionRepository.findAll().stream()
                .map(InscriptionDto::toDto)
                .collect(Collectors.toList());
    }

    // Get Inscription by ID
    public Optional<InscriptionDto> getInscriptionById(int id) {
        return inscriptionRepository.findById(id)
                .map(InscriptionDto::toDto);
    }

    // Create a new Inscription
    public InscriptionDto createInscription(InscriptionDto InscriptionDto) throws ResourceNotFoundException {
        // Fetch the associated etudiant
        Etudiant etudiant = etudiantRepository.findById(InscriptionDto.getEtudiant_id())
                .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id " + InscriptionDto.getEtudiant_id()));

        // Fetch the associated niveau
        Niveau niveau = niveauRepository.findById(InscriptionDto.getNiveau_id())
                .orElseThrow(() -> new ResourceNotFoundException("Niveau not found with id " + InscriptionDto.getNiveau_id()));

        // Build and save the Inscription
        Inscription inscription = inscriptionRepository.save(
                Inscription.builder()
                        .etudiant(etudiant)
                        .niveau(niveau)
                        .debutAnneUniversitaire(String.valueOf(LocalDate.now().getYear()))
                        .finAnneUniversitaire(String.valueOf(LocalDate.now().getYear() + 1))
                        .build()
        );

        // Convert the saved Inscription to InscriptionDto before returning
        return InscriptionDto.toDto(inscription);
    }

    // Update an existing Inscription
    public InscriptionDto updateInscription(int id, InscriptionDto InscriptionDetails) throws ResourceNotFoundException {
        Optional<Inscription> existingInscription = inscriptionRepository.findById(id);
        if (existingInscription.isPresent()) {
            Inscription updatedInscription = existingInscription.get();

            // Fetch the associated etudiant
            Etudiant etudiant = etudiantRepository.findById(InscriptionDetails.getEtudiant_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Etudiant not found with id " + InscriptionDetails.getEtudiant_id()));

            // Fetch the associated niveau
            Niveau niveau = niveauRepository.findById(InscriptionDetails.getNiveau_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Niveau not found with id " + InscriptionDetails.getNiveau_id()));

            // Update the Inscription details
            updatedInscription.setEtudiant(etudiant);
            updatedInscription.setNiveau(niveau);

            // Save the updated Inscription and convert to InscriptionDto before returning
            return InscriptionDto.toDto(inscriptionRepository.save(updatedInscription));
        } else {
            throw new ResourceNotFoundException("Inscription not found with id " + id);
        }
    }

    // Delete an Inscription
    public void deleteInscription(int id) throws ResourceNotFoundException {
        if (!inscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Inscription not found with id " + id);
        }
        inscriptionRepository.deleteById(id);
    }
}