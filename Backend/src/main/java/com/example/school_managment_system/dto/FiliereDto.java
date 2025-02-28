package com.example.school_managment_system.dto;

import lombok.Data;

@Data
public class FiliereDto {
    private int id;
    private String alias;
    private String intitule;
    private int anneeAccreditation;
    private int anneeFinAccreditation;
    private int coordonnateurId;
}
