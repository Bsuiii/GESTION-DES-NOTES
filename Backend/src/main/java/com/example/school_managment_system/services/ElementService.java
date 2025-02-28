package com.example.school_managment_system.services;


import com.example.school_managment_system.dto.ElementDto;
import com.example.school_managment_system.exceptions.ResourceAlreadyExistsException;
import com.example.school_managment_system.exceptions.ResourceNotFoundException;
import com.example.school_managment_system.models.Element;
import com.example.school_managment_system.models.Enseignant;
import com.example.school_managment_system.models.Module;
import com.example.school_managment_system.repositories.ElementRepository;
import com.example.school_managment_system.repositories.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ElementService {

    @Autowired
    private ElementRepository elementRepository;

    @Autowired
    private ModuleRepository moduleRepository;


    // Get all elements
    public List<ElementDto> getAllElements() {
        return elementRepository.findAll().stream()
                .map(ElementDto::toDto)
                .collect(Collectors.toList());
    }

    // Get element by ID
    public Optional<ElementDto> getElementById(int id) {
        return elementRepository.findById(id)
                .map(ElementDto::toDto);
    }

    // Create a new element
    public ElementDto createElement(ElementDto elementDto) throws ResourceNotFoundException {
        // Check if an element with the same code already exists
        if (elementRepository.findByCode(elementDto.getCode()).isPresent()) {
            throw new ResourceAlreadyExistsException("Element with code " + elementDto.getCode() + " already exists.");
        }

        // Fetch the associated module
        Module module = moduleRepository.findById(elementDto.getModule_id())
                .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + elementDto.getModule_id()));

        // Build and save the element
        Element element = elementRepository.save(
                Element.builder()
                        .titre(elementDto.getTitre())
                        .code(elementDto.getCode())
                        .module(module)
                        .build()
        );

        // Convert the saved element to ElementDto before returning
        return ElementDto.toDto(element);
    }

    // Update an existing element
    public ElementDto updateElement(int id, ElementDto elementDetails) throws ResourceNotFoundException {
        Optional<Element> existingElement = elementRepository.findById(id);
        if (existingElement.isPresent()) {
            Element updatedElement = existingElement.get();

            // Check if the new code conflicts with another element
            Optional<Element> elementWithSameCode = elementRepository.findByCode(elementDetails.getCode());
            if (elementWithSameCode.isPresent() && elementWithSameCode.get().getId() != id) {
                throw new ResourceAlreadyExistsException("Element with code " + elementDetails.getCode() + " already exists.");
            }

            // Fetch the associated module
            Module module = moduleRepository.findById(elementDetails.getModule_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Module not found with id " + elementDetails.getModule_id()));


            // Update the element details
            updatedElement.setTitre(elementDetails.getTitre());
            updatedElement.setCode(elementDetails.getCode());
            updatedElement.setModule(module);

            // Save the updated element and convert to ElementDto before returning
            return ElementDto.toDto(elementRepository.save(updatedElement));
        } else {
            throw new ResourceNotFoundException("Element not found with id " + id);
        }
    }

    // Delete an element
    public void deleteElement(int id) throws ResourceNotFoundException {
        if (!elementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Element not found with id " + id);
        }
        elementRepository.deleteById(id);
    }
}