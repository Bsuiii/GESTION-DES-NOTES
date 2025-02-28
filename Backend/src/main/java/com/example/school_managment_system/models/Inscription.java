package com.example.school_managment_system.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String debutAnneUniversitaire;
    private String finAnneUniversitaire;
    @ManyToOne
    @JoinColumn(name = "etudiant_id")
    private Etudiant etudiant;

    @ManyToOne
    @JoinColumn(name = "niveau_id")
    private Niveau niveau;
}
