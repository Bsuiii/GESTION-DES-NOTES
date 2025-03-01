package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Module;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {
    private int id;

    private String titre; // Exemple : "Programmation Java"
    private String code; // Exemple : "JAVA101"
    private String description; // Exemple : "Première année"
    private int filiere_id;
    private String filiere_intitule;

    private int niveau_id;
    private String niveau_alias;

    private int X,Y;


    public static ModuleDto toDto(Module module) {
        return ModuleDto.builder()
                .id(module.getId())
                .titre(module.getTitre())
                .code(module.getCode())
                .description(module.getDescription())
                .filiere_id(module.getFiliere().getId())
                .filiere_intitule(module.getFiliere().getIntitule())
                .niveau_id(module.getNiveau().getId())
                .niveau_alias(module.getNiveau().getAlias())

                .build();
    }
}
