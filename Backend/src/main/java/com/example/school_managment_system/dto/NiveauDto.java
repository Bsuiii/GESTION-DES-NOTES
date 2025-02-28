package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Niveau;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NiveauDto {
    private int id;
    private String niveau;
    private String alias;
    private int filiere_id;
    private String filiere_intitule;
    private Integer niveauSuivant_id;
    private String niveauSuivant_alias;

    public static NiveauDto toDto(Niveau niveau) {
        return NiveauDto.builder()
                .id(niveau.getId())
                .niveau(niveau.getNiveau())
                .alias(niveau.getAlias())
                .filiere_id(niveau.getFiliere().getId())
                .filiere_intitule(niveau.getFiliere().getIntitule())
                .niveauSuivant_id(niveau.getNiveauSuivant() != null ? niveau.getNiveauSuivant().getId() : null)
                .niveauSuivant_alias(niveau.getNiveauSuivant() != null ? niveau.getNiveauSuivant().getAlias() : null)
                .build();
    }
}