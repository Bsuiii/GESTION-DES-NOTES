package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Inscription;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InscriptionDto {
    private int id;
    private int etudiant_id; // ID of the associated etudiant
    private String etudiant_nom; // Name of the associated etudiant
    private String etudiant_prenom; // First name of the associated etudiant
    private int niveau_id; // ID of the associated niveau
    private String niveau_alias; // Alias of the associated niveau
    private String debutAnneUniversitaire ;
    private String finAnneUniversitaire ;

    public static InscriptionDto toDto(Inscription inscription) {
        return InscriptionDto.builder()
                .id(inscription.getId())
                .etudiant_id(inscription.getEtudiant().getId())
                .etudiant_nom(inscription.getEtudiant().getNom())
                .etudiant_prenom(inscription.getEtudiant().getPrenom())
                .niveau_id(inscription.getNiveau().getId())
                .niveau_alias(inscription.getNiveau().getAlias())
                .build();
    }
}