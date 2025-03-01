package com.example.school_managment_system.models;

import com.example.school_managment_system.utils.Semester;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "module")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre; // Exemple : "Programmation Java"
    private String code; // Exemple : "JAVA101"
    private String description; // Exemple : "Première année"

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Element> elements; // Liste des éléments associés à ce module


    @ManyToOne
    @JoinColumn(name="filiere_id")
    private Filiere filiere;

    @ManyToOne
    @JoinColumn(name="niveau_id")
    private Niveau niveau;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EnseignantModule> enseignantModules;

    @OneToMany(mappedBy = "module", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Note> notes;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Semester semestre;


}