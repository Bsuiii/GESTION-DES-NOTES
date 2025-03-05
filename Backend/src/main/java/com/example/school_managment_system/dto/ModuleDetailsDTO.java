package com.example.school_managment_system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleDetailsDTO {
    private String moduleName;
    private String semester;
    private String anneeUniversitaire;
    private String coordinateurNom;
    private String coordinateurPrenom;
    private String session;
    private String niveauAlias;
    private List<StudentDetailsDTO> students;

    @Data
    @Builder
    public static class StudentDetailsDTO {
        private int id;
        private String nom;
        private String prenom;
        private String cne;
        private List<ElementDetailsDTO> elements;
    }

    @Data
    @Builder
    public static class ElementDetailsDTO {
        private String elementName;
        private BigDecimal note;
        private String validation;
        private BigDecimal moyenne;
    }
}