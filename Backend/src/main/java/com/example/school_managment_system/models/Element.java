package com.example.school_managment_system.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "element")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Element {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String titre; // Exemple : "POO en Java"
    private String code; // Exemple : "JAVA101-POO"

    @ManyToOne
    private Module module;

    @OneToMany(mappedBy = "element", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Note> notes;
}