package com.example.school_managment_system.utils;

import com.example.school_managment_system.models.Etudiant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentComparison {
    private Etudiant oldData;
    private Etudiant newData;

}