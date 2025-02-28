package com.example.school_managment_system.dto;

import com.example.school_managment_system.models.Etudiant;
import lombok.*;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InscriptionCheckObject {

    private EtudiantDto current_info;
    private EtudiantDto new_info;
    private EtudiantDto chosen_info;
    private InscriptionDto inscription;
}
