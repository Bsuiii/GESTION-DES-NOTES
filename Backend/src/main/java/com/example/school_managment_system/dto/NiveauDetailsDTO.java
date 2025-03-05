package com.example.school_managment_system.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class NiveauDetailsDTO {
    private String niveauAlias;
    private String anneeUniversitaire;
    private LocalDate deliberationDate;
    private String session;
    private List<ModuleInfoDTO> modules;
    private List<StudentDTO> students;

    @Data
    @Builder
    public static class ModuleInfoDTO {
        private int id;
        private String name;
        private List<ElementInfoDTO> elements;
    }

    @Data
    @Builder
    public static class ElementInfoDTO {
        private int id;
        private String name;
    }

    @Data
    @Builder
    public static class StudentDTO {
        private int id;
        private String cne;
        private String nom;
        private String prenom;
        private List<ModuleResultDTO> moduleResults;
        private BigDecimal moyenne;
        private Integer rank;
    }

    @Data
    @Builder
    public static class ModuleResultDTO {
        private int moduleId;
        private String moduleName;
        private List<ElementResultDTO> elements;
        private BigDecimal moyenne;
        private String validation;
    }

    @Data
    @Builder
    public static class ElementResultDTO {
        private int elementId;
        private String elementName;
        private BigDecimal note;
    }
}
