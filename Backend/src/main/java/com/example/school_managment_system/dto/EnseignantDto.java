package com.example.school_managment_system.dto;
import com.example.school_managment_system.models.Enseignant;
import com.example.school_managment_system.models.EnseignantModule;
import com.example.school_managment_system.models.Filiere;
import com.example.school_managment_system.models.Module;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnseignantDto {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private List<Filiere> filieresCoordonnees; // IDs of Filieres coordinated by this Enseignant
    private List<EnseignantModule> enseignantModule; // IDs of EnseignantModules associated with this Enseignant

    public static EnseignantDto toDto(Enseignant enseignant) {
        return EnseignantDto.builder()
                .id(enseignant.getId())
                .nom(enseignant.getNom())
                .prenom(enseignant.getPrenom())
                .email(enseignant.getEmail())
                .filieresCoordonnees(enseignant.getFilieresCoordonnees() != null
                        ? enseignant.getFilieresCoordonnees().stream()
                        .map(filiere -> Filiere.builder()
                                .id(filiere.getId())
                                .alias(filiere.getAlias())
                                .build())
                        .collect(Collectors.toList())
                        : Collections.emptyList()) // Retourne une liste vide au lieu de null

                .enseignantModule(enseignant.getEnseignantModules() != null
                        ? enseignant.getEnseignantModules().stream()
                        .map(enseignantModule -> EnseignantModule.builder()
                                .id(enseignantModule.getId())
                                .start_date(enseignantModule.getStart_date())
                                .end_date(enseignantModule.getEnd_date())
                                .module(enseignantModule.getModule() != null // Vérifie que le module n'est pas null
                                        ? Module.builder()
                                        .id(enseignantModule.getModule().getId())
                                        .titre(enseignantModule.getModule().getTitre())
                                        .code(enseignantModule.getModule().getCode())
                                        .build()
                                        : null) // Si module est null, on ne le construit pas
                                .build())
                        .collect(Collectors.toList())
                        : Collections.emptyList()) // Retourne une liste vide si enseignantModules est null

                .build();

    }
}