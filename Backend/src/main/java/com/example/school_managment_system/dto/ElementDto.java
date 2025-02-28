package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Element;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ElementDto {
    private int id;
    private String titre; // Exemple : "POO en Java"
    private String code; // Exemple : "JAVA101-POO"
    private int module_id; // ID of the associated module
    private String module_titre; // Title of the associated module

    public static ElementDto toDto(Element element) {
        return ElementDto.builder()
                .id(element.getId())
                .titre(element.getTitre())
                .code(element.getCode())
                .module_id(element.getModule().getId())
                .module_titre(element.getModule().getTitre())
                .build();
    }
}