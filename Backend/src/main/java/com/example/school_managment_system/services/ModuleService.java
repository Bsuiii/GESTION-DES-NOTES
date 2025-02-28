package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.ModuleDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Filiere;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.models.Niveau;
import com.example.school_managment_system.repositories.FiliereRepository;
import com.example.school_managment_system.repositories.ModuleRepository;
import com.example.school_managment_system.repositories.NiveauRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
}