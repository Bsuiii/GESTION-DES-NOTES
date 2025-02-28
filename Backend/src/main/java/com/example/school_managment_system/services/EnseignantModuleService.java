package com.example.school_managment_system.services;

import com.example.school_managment_system.dto.EnseignantModuleDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Enseignant;
import com.example.school_managment_system.models.EnseignantModule;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.repositories.EnseignantModuleRepository;
import com.example.school_managment_system.repositories.EnseignantRepository;
import com.example.school_managment_system.repositories.ModuleRepository;
import com.example.school_managment_system.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EnseignantModuleService {

    @Autowired
    private EnseignantModuleRepository enseignantModuleRepository;

    @Autowired
    private EnseignantRepository enseignantRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    // Get all enseignantModules
    public List<EnseignantModuleDto> getAllEnseignantModules() {
        return enseignantModuleRepository.findAll().stream()
                .map(EnseignantModuleDto::toDto)
                .collect(Collectors.toList());
    }

    // Get enseignantModule by ID
    public Optional<EnseignantModuleDto> getEnseignantModuleById(int id) {
        return enseignantModuleRepository.findById(id)
                .map(EnseignantModuleDto::toDto);
    }

    // Create a new enseignantModule
    public EnseignantModuleDto createEnseignantModule(EnseignantModuleDto enseignantModuleDto) throws ResourceNotFoundException {
        // Fetch the associated module
        Module module = moduleRepository.findById(enseignantModuleDto.getModule_id())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + enseignantModuleDto.getModule_id()));

        // Fetch the associated enseignant
        Enseignant enseignant = enseignantRepository.findById(enseignantModuleDto.getEnseignant_id())
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant not found with id " + enseignantModuleDto.getEnseignant_id()));


        // Build and save the enseignantModule
        EnseignantModule enseignantModule = enseignantModuleRepository.save(
                EnseignantModule.builder()
                        .start_date(Utils.dateFormatter(enseignantModuleDto.getStart_date()))
                        .end_date(Utils.dateFormatter(enseignantModuleDto.getEnd_date()))
                        .module(module)
                        .enseignant(enseignant)
                        .build()
        );

        // Convert the saved enseignantModule to EnseignantModuleDto before returning
        return EnseignantModuleDto.toDto(enseignantModule);
    }

    // Update an existing enseignantModule
    public EnseignantModuleDto updateEnseignantModule(int id, EnseignantModuleDto enseignantModuleDetails) throws ResourceNotFoundException {
        Optional<EnseignantModule> existingEnseignantModule = enseignantModuleRepository.findById(id);
        if (existingEnseignantModule.isPresent()) {
            EnseignantModule updatedEnseignantModule = existingEnseignantModule.get();

            // Fetch the associated module
           Module module = moduleRepository.findById(enseignantModuleDetails.getModule_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + enseignantModuleDetails.getModule_id()));

            // Fetch the associated enseignant
            Enseignant enseignant = enseignantRepository.findById(enseignantModuleDetails.getEnseignant_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Enseignant not found with id " + enseignantModuleDetails.getEnseignant_id()));

            // Update the enseignantModule details
            // Update the enseignantModule details
            updatedEnseignantModule.setStart_date(Utils.dateFormatter(enseignantModuleDetails.getStart_date()));
            updatedEnseignantModule.setEnd_date(Utils.dateFormatter(enseignantModuleDetails.getEnd_date()));
            updatedEnseignantModule.setModule(module);
            updatedEnseignantModule.setEnseignant(enseignant);

            // Save the updated enseignantModule and convert to EnseignantModuleDto before returning
            return EnseignantModuleDto.toDto(enseignantModuleRepository.save(updatedEnseignantModule));

        } else {

            throw new ResourceNotFoundException("EnseignantModule not found with id " + id);
        }
    }

    // Delete an enseignantModule
    public void deleteEnseignantModule(int id) throws ResourceNotFoundException {
        if (!enseignantModuleRepository.existsById(id)) {
            throw new ResourceNotFoundException("EnseignantModule not found with id " + id);
        }
        enseignantModuleRepository.deleteById(id);
    }
}