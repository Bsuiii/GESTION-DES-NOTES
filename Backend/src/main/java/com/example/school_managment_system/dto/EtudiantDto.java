package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Etudiant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EtudiantDto {
    private int id;
    private String nom;
    private String prenom;
    private String cne;


    public static EtudiantDto toDto(Etudiant etudiant) {
        return EtudiantDto.builder()
                .id(etudiant.getId())
                .nom(etudiant.getNom())
                .prenom(etudiant.getPrenom())
                .cne(etudiant.getCne())
                .build();
    }
}