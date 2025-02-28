package com.example.school_managment_system.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "filiere")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Filiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String alias; // Exemple : "GI1", "GI2"
    private String intitule; // Exemple : "Génie Informatique"
    private int anneeAccreditation; // Exemple : 2020
    private int anneeFinAccreditation; // Exemple : 2025

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Niveau> niveaux; // Liste des niveaux associés à cette filière

    @ManyToOne
    @JoinColumn(name = "coordonnateur_id", unique = true, foreignKey = @ForeignKey(name = "fk_filiere_coordonnateur"))
    private Enseignant coordonnateur;

    @OneToMany(mappedBy = "filiere", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Module> modules;
}